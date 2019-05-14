package com.mobius.software.android.iotbroker.main.iot_protocols.coap;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mobius.software.android.iotbroker.main.base.ApplicationSettings;
import com.mobius.software.android.iotbroker.main.iot_protocols.IotProtocol;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.QoS;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.TimersMap;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapCode;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapOptionType;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapType;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.OptionParser;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.parser.CoapParser;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.MQParser;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MQTopic;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MessageType;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.Text;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.Will;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Connect;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Disconnect;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Pingreq;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Publish;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Subscribe;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Unsubscribe;
import com.mobius.software.android.iotbroker.main.listeners.ClientStateListener;
import com.mobius.software.android.iotbroker.main.listeners.DataBaseListener;
import com.mobius.software.android.iotbroker.main.managers.ConnectionState;
import com.mobius.software.android.iotbroker.main.managers.ConnectionTimerTask;
import com.mobius.software.android.iotbroker.main.managers.MessageResendTimerTask;
import com.mobius.software.android.iotbroker.main.net.DtlsClient;
import com.mobius.software.android.iotbroker.main.net.InternetProtocol;
import com.mobius.software.android.iotbroker.main.net.TCPClient;
import com.mobius.software.android.iotbroker.main.net.TLSHelper;
import com.mobius.software.android.iotbroker.main.net.UDPClient;
import com.mobius.software.android.iotbroker.main.services.NetworkService;
import com.mobius.software.android.iotbroker.main.utility.ConvertorUtil;
import com.mobius.software.android.iotbroker.main.utility.FileManager;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

public class CoapClient implements IotProtocol {

    private InetSocketAddress address;
    private ConnectionState connectionState;

    private TimersMap timers;
    private InternetProtocol client;
    private String clientID;
    private boolean isClean;
    private int keepalive;
    private Context context;
    private ClientStateListener listener;
    private Timer timer = new Timer();
    private ConnectionTimerTask connectionCheckTask;
    private boolean isSecure = false;
    private DataBaseListener dbListener;
    private static ConnectionState currentState;
    private HashMap<Integer, String> topics;
    private int connectCount=0;

    public CoapClient(InetSocketAddress address, String clientID, boolean isClean,
                      int keepalive, Context context) {
        this.address = address;
        this.clientID = clientID;
        this.isClean = isClean;
        this.keepalive = keepalive;
        this.context = context;
        this.client = new UDPClient(address, workerThreads);
        this.topics = new HashMap<>();
    }

    public CoapClient(InetSocketAddress address, String clientID, boolean isClean,
                      int keepalive, String crt, String crtPass, Context context) {
        this.isSecure = true;
        this.address = address;
        this.clientID = clientID;
        this.isClean = isClean;
        this.keepalive = keepalive;
        this.context = context;
        this.client = new DtlsClient(address, workerThreads);
        ((DtlsClient)this.client).setSecure(this.isSecure);

        try {
            ((DtlsClient) this.client).setKeyStore(TLSHelper.getKeyStore(crt, crtPass));
            ((DtlsClient) this.client).setKeyStorePassword(crtPass);
        }
        catch(Exception ex) {

        }

        this.topics = new HashMap<>();
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
        Boolean isSuccess = client.init(this, new CoapParser());
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

    @Override
    public void send(Message message) {
        client.send(message);
    }

    public void connect() {
        setState(ConnectionState.CONNECTING);

        if (timers != null)
            timers.stopAllTimers();

        send(getPingreqMessage());
        timers = new TimersMap(this, client);
        timers.startPingTimer(keepalive);
    }

    public void disconnect() {
        this.closeConnection();
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
    }

    public Message getPingreqMessage()
    {
        CoapHeader coapMessage = new CoapHeader(CoapCode.PUT, true, "");
        coapMessage.addOption(CoapOptionType.NODE_ID, this.clientID);
        return coapMessage;
    }

    public void subscribe(String topicName, QoS qos) {

        byte qosNumber = (byte)(qos.getValue() >= 1 ? 1 : qos.getValue());

        CoapHeader coapMessage = new CoapHeader(CoapCode.GET, true, "");
        coapMessage.addOption(OptionParser.encode(CoapOptionType.NODE_ID, this.clientID));
        coapMessage.addOption(OptionParser.encode(CoapOptionType.ACCEPT, qosNumber));
        coapMessage.addOption(OptionParser.encode(CoapOptionType.OBSERVE, 0));
        coapMessage.addOption(OptionParser.encode(CoapOptionType.URI_PATH, topicName));
        int number = timers.store(coapMessage);
        client.send(coapMessage);
        this.topics.put(number, topicName);
    }

    public void unsubscribe(String topicName, QoS qos) {

        byte qosNumber = (byte)(qos.getValue() >= 1 ? 1 : qos.getValue());

        CoapHeader coapMessage = new CoapHeader(CoapCode.GET, true, "");
        coapMessage.addOption(OptionParser.encode(CoapOptionType.NODE_ID, this.clientID));
        coapMessage.addOption(OptionParser.encode(CoapOptionType.ACCEPT, qosNumber));
        coapMessage.addOption(OptionParser.encode(CoapOptionType.OBSERVE, 1));
        coapMessage.addOption(OptionParser.encode(CoapOptionType.URI_PATH, topicName));
        int number = timers.store(coapMessage);
        client.send(coapMessage);
        this.topics.put(number, topicName);
    }

    public void publish(String topicName, QoS qos, byte[] content, boolean retain, boolean dup) {

        byte qosNumber = (byte)(qos.getValue() >= 1 ? 1 : qos.getValue());

        CoapHeader coapMessage = new CoapHeader(CoapCode.PUT, true, new String(content));
        coapMessage.addOption(OptionParser.encode(CoapOptionType.NODE_ID, this.clientID));
        coapMessage.addOption(OptionParser.encode(CoapOptionType.ACCEPT, qosNumber));
        coapMessage.addOption(OptionParser.encode(CoapOptionType.URI_PATH, topicName));
        int number = timers.store(coapMessage);
        client.send(coapMessage);
        this.topics.put(number, topicName);
    }

    public void reinit() {
        connectCount=0;
        setState(ConnectionState.CHANNEL_CREATING);

        if (client != null)
            client.shutdown();

        if (this.isSecure) {
            client = new DtlsClient(address, workerThreads);
        } else {
            client = new UDPClient(address, workerThreads);
        }
    }

    public void closeConnection() {
        if (timers != null)
            timers.stopAllTimers();

        if (client != null) {
            if (this.isSecure) {
                DtlsClient currClient = (DtlsClient)client;
                client = null;
                currClient.shutdown();
            } else {
                UDPClient currClient = (UDPClient)client;
                client = null;
                currClient.shutdown();
            }
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
    }

    private void writeTopics(String topicName, int qos) {
        if (dbListener.writeTopics(topicName, qos)) {
            sendMessageIntent(MessageType.SUBACK);
        }
    }

    @Override
    public void processMessage(Message coapMessage) {

        if (coapMessage.getProtocol() != Protocols.COAP_PROTOCOL) {
            return;
        }

        CoapType type = CoapType.valueOf(coapMessage.getType());
        CoapHeader message = (CoapHeader)coapMessage;

        if ((message.getCode() == CoapCode.POST || message.getCode() == CoapCode.PUT) && type != CoapType.ACKNOWLEDGEMENT) {
            Short qos = (Short) OptionParser.decode(CoapOptionType.ACCEPT, message.getOption(CoapOptionType.ACCEPT).getValue());
            String topic = (String) OptionParser.decode(CoapOptionType.URI_PATH, message.getOption(CoapOptionType.URI_PATH).getValue());
            if (topic != null && topic.length() > 0) {
                byte[] content = message.getPayload();
                try {
                    String contentMessage = new String(content, "UTF-8");
                    dbListener.addMessage(contentMessage, qos, true, topic);
                } catch (UnsupportedEncodingException e) {

                }
            } else {
                CoapHeader ack = new CoapHeader(CoapCode.BAD_OPTION, false, "");
                ack.addOption(CoapOptionType.CONTENT_FORMAT, "text/plain");
                ack.setCoapType(CoapType.ACKNOWLEDGEMENT);
                ack.setMessageID(message.getMessageID());
                ack.setToken(message.getToken());
                client.send(ack);
                return;
            }
        }

        switch (type) {
            case CONFIRMABLE:
            {
                CoapHeader ack = new CoapHeader(CoapCode.PUT, true, "");
                ack.addOption(OptionParser.encode(CoapOptionType.NODE_ID, this.clientID));
                ack.setCoapType(CoapType.ACKNOWLEDGEMENT);
                ack.setMessageID(message.getMessageID());
                ack.setToken(message.getToken());
                client.send(ack);
            } break;
            case NON_CONFIRMABLE:
            {
                timers.remove(ConvertorUtil.bytesToInt(message.getToken()));
            } break;
            case ACKNOWLEDGEMENT:
            {
                if (message.getToken() == null) {
                    if(currentState()==ConnectionState.CONNECTING)
                        setState(ConnectionState.CONNECTION_ESTABLISHED);
                    return;
                }
                MessageResendTimerTask timerItem = this.timers.remove(ConvertorUtil.bytesToInt(message.getToken()));
                if (timerItem != null) {
                    CoapHeader ack = (CoapHeader)timerItem.retrieveMessage();
                    if (ack != null) {
                        if (ack.getCode() == CoapCode.CONTENT) {
                            Short qos = (Short) OptionParser.decode(CoapOptionType.ACCEPT, ack.getOption(CoapOptionType.ACCEPT).getValue());
                            String topic = this.topics.get(ConvertorUtil.bytesToInt(ack.getToken()));
                            byte[] content = ack.getPayload();
                            try {
                                String contentMessage = new String(content, "UTF-8");
                                dbListener.addMessage(contentMessage, qos, true, topic);
                            } catch (UnsupportedEncodingException e) {

                            }
                        }
                        if (ack.getCode() == CoapCode.GET) {
                            Short qos = (Short) OptionParser.decode(CoapOptionType.ACCEPT, ack.getOption(CoapOptionType.ACCEPT).getValue());
                            Integer observe = (Integer) OptionParser.decode(CoapOptionType.OBSERVE, ack.getOption(CoapOptionType.OBSERVE).getValue());
                            String topic = this.topics.get(ConvertorUtil.bytesToInt(ack.getToken()));
                            if (observe != null) {
                                if (observe == 0) {
                                    dbListener.writeTopics(topic, qos);
                                    sendMessageIntent(MessageType.SUBACK);
                                } else if (observe == 1) {
                                    dbListener.deleteTopics(new Text(topic));
                                    sendMessageIntent(MessageType.UNSUBACK);
                                }
                            }
                        }
                    }
                }
            } break;
            case RESET:
            {
                timers.remove(ConvertorUtil.bytesToInt(message.getToken()));
            } break;
        }
    }

    public void sendMessageIntent(MessageType messageType) {

        Intent startServiceIntent = new Intent(context, NetworkService.class);

        startServiceIntent.putExtra(ApplicationSettings.PARAM_MESSAGETYPE, Integer.toString(messageType.getNum()));
        startServiceIntent.setAction(ApplicationSettings.ACTION_MESSAGE_RECEIVED);
        context.startService(startServiceIntent);
    }

    public void executeTimer(TimerTask task, long period) {
        if(currentState()==ConnectionState.CONNECTING) {
            //its ping
            if (this.connectCount > 0) {
                timers.stopAllTimers();
                client.shutdown();
                setState(ConnectionState.CHANNEL_FAILED);
                return;
            }
            this.connectCount += 1;
        }

        timer.schedule(task, period);
    }

    @Override
    public void timeout() {
        this.closeConnection();
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
