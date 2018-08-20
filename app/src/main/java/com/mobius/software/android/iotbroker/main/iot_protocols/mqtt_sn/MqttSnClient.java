package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mobius.software.android.iotbroker.main.CoreLogicException;
import com.mobius.software.android.iotbroker.main.base.ApplicationSettings;
import com.mobius.software.android.iotbroker.main.iot_protocols.IotProtocol;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.QoS;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.TimersMap;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Topic;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.MQParser;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MessageType;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.Text;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.Will;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.Parser;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps.FullTopic;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps.IdentifierTopic;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps.ReturnCode;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps.SNType;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.Regack;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.Register;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.SNConnack;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.SNConnect;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.SNDisconnect;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.SNPingreq;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.SNPuback;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.SNPubcomp;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.SNPublish;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.SNPubrec;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.SNPubrel;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.SNSuback;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.SNSubscribe;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.SNUnsuback;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.SNUnsubscribe;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.WillMsg;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.WillTopic;
import com.mobius.software.android.iotbroker.main.listeners.ClientStateListener;
import com.mobius.software.android.iotbroker.main.listeners.DataBaseListener;
import com.mobius.software.android.iotbroker.main.managers.ConnectionState;
import com.mobius.software.android.iotbroker.main.managers.MessageResendTimerTask;
import com.mobius.software.android.iotbroker.main.net.UDPClient;
import com.mobius.software.android.iotbroker.main.managers.ConnectionTimerTask;
import com.mobius.software.android.iotbroker.main.services.NetworkService;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Mobius Software LTD
 * Copyright 2015-2017, Mobius Software LTD
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
public class MqttSnClient implements IotProtocol {

    private InetSocketAddress address;
    private ConnectionState connectionState;

    private TimersMap timers;
    private UDPClient client;
    private String username;
    private String password;
    private String clientID;
    private boolean isClean;
    private int keepalive;
    private Will will;
    private Context context;
    private ClientStateListener listener;
    private java.util.Timer timer = new java.util.Timer();
    private ConnectionTimerTask connectionCheckTask;

    private Map<Integer, SNPublish> listForPublish;
    private Map<Integer, Message> publishPackets;

    private DataBaseListener dbListener;
    private static ConnectionState currentState;

    private int counter;

    public MqttSnClient(InetSocketAddress address, String username, String password, String clientID, boolean isClean,
                        int keepalive, Will will, Context context) {

        this.publishPackets = new HashMap<>();
        this.listForPublish = new HashMap<>();

        this.address = address;
        this.username = username;
        this.password = password;
        this.clientID = clientID;
        this.isClean = isClean;
        this.keepalive = keepalive;
        this.will = will;
        this.context = context;
        this.client = new UDPClient(address, workerThreads);
        this.counter = 0;
    }

    public void setListener(ClientStateListener listener) {
        this.listener = listener;
    }

    public void setDbListener(DataBaseListener dbListener) {
        this.dbListener = dbListener;
    }

    private void setState(ConnectionState state) {
        this.connectionState = state;

        currentState = state;

        Intent startServiceIntent = new Intent(context, NetworkService.class);

        startServiceIntent.putExtra(ApplicationSettings.PARAM_STATE, state.toString());
        startServiceIntent.setAction(ApplicationSettings.STATE_CHANGED);
        context.startService(startServiceIntent);
    }

    public Boolean createChannel() {
        setState(ConnectionState.CHANNEL_CREATING);
        Boolean isSuccess = client.init(this, new Parser());
        if (!isSuccess)
            setState(ConnectionState.CHANNEL_FAILED);
        else {
            connectionCheckTask = new ConnectionTimerTask(this);
            executeTimer(connectionCheckTask, ConnectionTimerTask.REFRESH_PERIOD);
        }

        return isSuccess;
    }

    public boolean checkCreated() {
        Boolean isConnected = client.isConnected();
        if (isConnected && connectionState == ConnectionState.CHANNEL_CREATING)
            setState(ConnectionState.CHANNEL_ESTABLISHED);

        return client.isConnected();
    }

    public boolean checkConnected() {
        return connectionState == ConnectionState.CONNECTION_ESTABLISHED;
    }

    public ConnectionState getConnectionState() {
        return connectionState;
    }

    public void closeChannel() {
        client.shutdown();
    }

    public void connect() {
        setState(ConnectionState.CONNECTING);

        SNConnect connect = new SNConnect(isClean, keepalive, clientID, (will != null));

        if (timers != null)
            timers.stopAllTimers();
        timers = null;

        if (timers == null) {

            timers = new TimersMap(this, client);
            timers.storeConnectTimer(connect);
            if (client.isConnected()) {
                client.send(connect);
            }
        }
    }

    public void disconnect() {
        if (client.isConnected()) {
            client.send(new SNDisconnect(1));
            client.close();

            if (timers != null)
                timers.stopAllTimers();
            timers = null;

            if (connectionCheckTask != null) {
                connectionCheckTask.cancel();
            }
        }
    }

    public Message getPingreqMessage() {
        return new SNPingreq(this.clientID);
    }

    public void subscribe(String topicName, QoS qos) {

        FullTopic topic = new FullTopic(topicName, qos);
        SNSubscribe subscribe = new SNSubscribe(0, topic, false);

        timers.store(subscribe);
        client.send(subscribe);
    }

    public void unsubscribe(String topicName, QoS qos) {

        FullTopic topic = new FullTopic(topicName, qos);
        SNUnsubscribe unsubscribe = new SNUnsubscribe(0, topic);

        timers.store(unsubscribe);
        client.send(unsubscribe);
    }

    public void publish(String topicName, QoS qos, byte[] content, boolean retain, boolean dup) {

        Register register = new Register(0, 0, topicName);

        ByteBuf wrappedBuffer = Unpooled.wrappedBuffer(content);
        FullTopic topic = new FullTopic(topicName, qos);

        if (topic.getQos() != QoS.AT_MOST_ONCE) {
            timers.store(register);
        } else {
            this.counter = ((this.counter + 1) >= 65535) ? 1 : ++this.counter;
            this.listForPublish.put(this.counter, new SNPublish(this.counter, topic, wrappedBuffer, dup, retain));
            register.setPacketID(this.counter);
        }
        client.send(register);
    }

    public void reinit() {

        setState(ConnectionState.CHANNEL_CREATING);

        if (client != null)
            client.shutdown();

        client = new UDPClient(address, workerThreads);
    }

    public void closeConnection() {
        if (timers != null)
            timers.stopAllTimers();

        if (client != null) {
            UDPClient currClient = client;
            client = null;
            currClient.shutdown();
        }
    }

    @Override
    public void packetReceived(Message message) {
        try {
            message.processBy(this);
        }
        catch (Exception e) {
            e.printStackTrace();
            client.shutdown();
        }
    }

    @Override
    public void connectionLost() {
        if (isClean)
            cleanCurrentSession();
        setState(ConnectionState.CONNECTION_LOST);

        if (timers != null)
            timers.stopAllTimers();
    }

    private void cleanCurrentSession() {
        dbListener.clearTopicByActiveAccount();
        dbListener.deleteMessages();
    }

    private void writeTopics(String topicName, int qos) {
        if (dbListener.writeTopics(topicName, qos)) {
            sendMessageIntent(MessageType.SUBACK);
        }
    }

    @Override
    public void processMessage(Message message) {

        if (message.getProtocol() != Protocols.MQTT_SN_PROTOCOL) {
            return;
        }

        SNType type = SNType.valueOf(message.getType());
        Log.v("TAG", " -> "+type.toString());

        switch (type) {
            case ADVERTISE:
            {
                throw new CoreLogicException("received invalid message advertise");
            }
            case SEARCHGW:
            {
                throw new CoreLogicException("received invalid message searchgw");
            }
            case GWINFO:
            {
                throw new CoreLogicException("received invalid message gwinfo");
            }
            case CONNECT:
            {
                throw new CoreLogicException("received invalid message connect");
            }
            case CONNACK:
            {
                SNConnack connack = (SNConnack)message;
                MessageResendTimerTask timer = timers.stopConnectTimer();

                if (connack.getCode() == ReturnCode.ACCEPTED) {
                    setState(ConnectionState.CONNECTION_ESTABLISHED);

                    if (timer != null) {
                        SNConnect connect = (SNConnect) timer.retrieveMessage();
                        if (connect.isCleanSession()) {
                            cleanCurrentSession();
                        }
                    }
                    timers.startPingTimer(keepalive);
                } else {
                    timers.stopAllTimers();
                    client.shutdown();
                    setState(ConnectionState.CHANNEL_FAILED);
                }
            } break;
            case WILL_TOPIC_REQ:
            {
                timers.stopConnectTimer();
                FullTopic fullTopic = new FullTopic(will.getTopic().getName().toString(), will.getTopic().getQos());
                WillTopic willTopic = new WillTopic(will.getRetain(), fullTopic);
                client.send(willTopic);
            } break;
            case WILL_TOPIC:
            {
                throw new CoreLogicException("received invalid message will topic");
            }
            case WILL_MSG_REQ:
            {
                throw new CoreLogicException("received invalid message will msg req");
            }
            case WILL_MSG:
            {
                ByteBuf wrappedBuffer = Unpooled.wrappedBuffer(will.getContent());
                WillMsg willMsg = new WillMsg(wrappedBuffer);
                client.send(willMsg);
            } break;
            case REGISTER:
            {
                Register register = (Register)message;
                Regack regack = new Regack(register.getTopicID(), register.getPacketID(), ReturnCode.ACCEPTED);
                client.send(regack);
            } break;
            case REGACK:
            {
                Regack regack = (Regack)message;
                timers.remove(regack.getPacketID());

                if (regack.getCode() == ReturnCode.ACCEPTED) {
                    SNPublish publish = this.listForPublish.get(regack.getTopicID());
                    if (publish != null) {
                        IdentifierTopic topic = new IdentifierTopic(regack.getTopicID(), publish.getTopic().getQos());
                        publish.setPacketID(regack.getPacketID());
                        publish.setTopic(topic);
                        if (publish.getTopic().getQos() != QoS.AT_MOST_ONCE) {
                            timers.store(publish);
                        }
                        client.send(publish);
                    }
                }
            } break;
            case PUBLISH:
            {
                SNPublish publish = (SNPublish)message;
                QoS publisherQos = publish.getTopic().getQos();
                switch (publisherQos) {
                    case AT_LEAST_ONCE:
                        int topicID = Integer.parseInt(publish.getTopic().encode().toString());
                        SNPuback puback = new SNPuback(topicID, publish.getPacketID(), ReturnCode.ACCEPTED);
                        client.send(puback);
                        break;
                    case EXACTLY_ONCE:
                        SNPubrec pubrec = new SNPubrec(publish.getPacketID());
                        publishPackets.put(publish.getPacketID(), publish);
                        client.send(pubrec);
                        break;
                    default:
                        break;
                }

                sendMessageIntent(MessageType.PUBLISH);
                if (dbListener.isTopicExist(Arrays.toString(publish.getTopic().encode()))) {
                    return;
                }

                if (!(publish.isDup() && publisherQos == QoS.EXACTLY_ONCE)) {
                    String contentMessage;
                    try {
                        contentMessage = new String(publish.getContent().array(), "UTF-8");
                        dbListener.addMessage(contentMessage, publisherQos.getValue(), true, Arrays.toString(publish.getTopic().encode()));
                    }
                    catch (UnsupportedEncodingException e) {

                    }
                }
            } break;
            case PUBACK:
            {
                SNPuback puback = (SNPuback)message;
                timers.remove(puback.getPacketID());
                sendMessageIntent(MessageType.PUBACK);
            } break;
            case PUBCOMP:
            {
                SNPubcomp pubcomp = (SNPubcomp)message;
                timers.remove(pubcomp.getPacketID());
                sendMessageIntent(MessageType.PUBCOMP);
            } break;
            case PUBREC:
            {
                SNPubrec pubrec = (SNPubrec)message;
                SNPubrel pubrel = new SNPubrel(pubrec.getPacketID());
                timers.refreshTimer(pubrel);
                client.send(pubrel);
            } break;
            case PUBREL:
            {
                SNPubrel pubrel = (SNPubrel)message;
                client.send(new SNPubcomp(pubrel.getPacketID()));
            } break;
            case SUBSCRIBE:
            {
                throw new CoreLogicException("received invalid message subscribe");
            }
            case SUBACK:
            {
                SNSuback suback = (SNSuback)message;
                MessageResendTimerTask timer = timers.remove(suback.getPacketID());

                if (suback.getCode() == ReturnCode.ACCEPTED) {
                    SNSubscribe subscribe = (SNSubscribe)timer.retrieveMessage();
                    Topic topic = subscribe.getTopic();
                    QoS expectedQos = topic.getQos();
                    int qos = expectedQos.getValue();
                    writeTopics(Arrays.toString(topic.encode()), qos);
                }
            } break;
            case UNSUBSCRIBE:
            {
                throw new CoreLogicException("received invalid message unsubscribe");
            }
            case UNSUBACK:
            {
                SNUnsuback unsuback = (SNUnsuback)message;
                MessageResendTimerTask timer = timers.remove(unsuback.getPacketID());

                if (timer != null) {
                    SNUnsubscribe unsubscribe = (SNUnsubscribe) timer.retrieveMessage();
                    Topic topic = unsubscribe.getTopic();

                    dbListener.deleteTopics(new Text(topic.encode().toString()));
                }

                sendMessageIntent(MessageType.UNSUBACK);
            } break;
            case PINGREQ:
            {
                throw new CoreLogicException("received invalid message pingreq");
            }
            case PINGRESP:
            {

            } break;
            case DISCONNECT:
            {
                timers.stopAllTimers();
            } break;
            case WILL_TOPIC_UPD:
            {
                throw new CoreLogicException("received invalid message will topic upd");
            }
            case WILL_TOPIC_RESP:
            {
                throw new CoreLogicException("received invalid message will topic resp");
            }
            case WILL_MSG_UPD:
            {
                throw new CoreLogicException("received invalid message will msg upd");
            }
            case WILL_MSG_RESP:
            {
                throw new CoreLogicException("received invalid message will msg resp");
            }
            case ENCAPSULATED:
            {
                throw new CoreLogicException("received invalid message encapsulated");
            }
        }
    }

    public void sendMessageIntent(MessageType messageType) {

        Intent startServiceIntent = new Intent(context, NetworkService.class);

        startServiceIntent.putExtra(ApplicationSettings.PARAM_MESSAGETYPE, Integer.toString(messageType.getNum()));
        startServiceIntent.setAction(ApplicationSettings.ACTION_MESSAGE_RECEIVED);
        context.startService(startServiceIntent);
    }

    public void executeTimer(java.util.TimerTask task, long period) {
        timer.schedule(task, period);
    }

    @Override
    public void writeError() {
        if (this.listener != null)
            listener.writeError();
    }

    public ConnectionState currentState() {
        return currentState;
    }
}
