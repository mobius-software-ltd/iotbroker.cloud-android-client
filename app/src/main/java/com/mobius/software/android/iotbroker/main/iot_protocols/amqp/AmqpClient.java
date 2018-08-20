package com.mobius.software.android.iotbroker.main.iot_protocols.amqp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mobius.software.android.iotbroker.main.CoreLogicException;
import com.mobius.software.android.iotbroker.main.base.ApplicationSettings;
import com.mobius.software.android.iotbroker.main.iot_protocols.IotProtocol;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.AMQPTransferMap;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.HeaderCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.OutcomeCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.RoleCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.SendCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.TerminusDurability;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPAttach;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPBegin;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPClose;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPDetach;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPDisposition;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPEnd;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPFlow;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPOpen;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPTransfer;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPPing;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPProtoHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headersasl.SASLInit;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headersasl.SASLMechanisms;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headersasl.SASLOutcome;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.AMQPData;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.AMQPSection;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.MessageHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.terminus.AMQPSource;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.terminus.AMQPTarget;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described.AMQPAccepted;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.AMQPMessageFormat;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.AMQPSymbol;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.parser.AMQPParser;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.QoS;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.TimersMap;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.MQParser;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.ConnackCode;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MQTopic;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MessageType;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.SubackCode;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.Text;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.Will;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Connack;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Connect;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Disconnect;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Pingreq;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Puback;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Pubcomp;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Publish;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Pubrec;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Pubrel;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Suback;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Subscribe;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Unsuback;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Unsubscribe;
import com.mobius.software.android.iotbroker.main.listeners.ClientStateListener;
import com.mobius.software.android.iotbroker.main.listeners.DataBaseListener;
import com.mobius.software.android.iotbroker.main.managers.ConnectionState;
import com.mobius.software.android.iotbroker.main.managers.ConnectionTimerTask;
import com.mobius.software.android.iotbroker.main.managers.MessageResendTimerTask;
import com.mobius.software.android.iotbroker.main.net.TCPClient;
import com.mobius.software.android.iotbroker.main.services.NetworkService;
import com.mobius.software.android.iotbroker.main.utility.FileManager;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

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

public class AmqpClient implements IotProtocol {

    private InetSocketAddress address;
    private ConnectionState connectionState;

    private TimersMap timers;
    private TCPClient client;
    private String username;
    private String password;
    private String clientID;
    private boolean isClean;
    private int keepalive;
    private Will will;
    private Context context;
    private ClientStateListener listener;
    private Timer timer = new Timer();
    private ConnectionTimerTask connectionCheckTask;

    private boolean isSASLСonfirm;
    private int chanel;

    private DataBaseListener dbListener;
    private static ConnectionState currentState;

    private Long nextHandle;

    private Map<String, Long> usedIncomingMappings = new HashMap<>();
    private Map<String, Long> usedOutgoingMappings = new HashMap<>();
    private Map<Long, String> usedMappings = new HashMap<>();
    private ArrayList<AMQPTransfer> pendingMessages = new ArrayList<>();

    public AmqpClient(InetSocketAddress address, String username, String password, String clientID, boolean isClean,
                           int keepalive, Will will, Context context) {

        this.address = address;
        this.username = username;
        this.password = password;
        this.clientID = clientID;
        this.isClean = isClean;
        this.keepalive = keepalive;
        this.will = will;
        this.context = context;
        this.client = new TCPClient(address, workerThreads);
        this.client.setSecure(false);
        this.isSASLСonfirm = false;
        this.chanel = 0;
        this.nextHandle = 0L;
    }

    public AmqpClient(InetSocketAddress address, String username, String password, String clientID, boolean isClean,
                      int keepalive, Will will, String crtPath, String crtPass, Context context) {

        this.address = address;
        this.username = username;
        this.password = password;
        this.clientID = clientID;
        this.isClean = isClean;
        this.keepalive = keepalive;
        this.will = will;
        this.context = context;
        this.isSASLСonfirm = false;
        this.chanel = 0;
        this.client = new TCPClient(address, workerThreads);
        this.client.setSecure(true);
        this.client.setKeyStore(FileManager.loadKeyStore(crtPath, crtPass));
        this.client.setKeyStorePassword(crtPass);
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
        Boolean isSuccess = client.init(this, new AMQPParser());
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
        timers = new TimersMap(this, client);

        AMQPProtoHeader header = new AMQPProtoHeader(3);
        client.send(header);
    }

    public void disconnect() {
        if (client.isConnected()) {
            AMQPEnd end = new AMQPEnd();
            end.setChannel(this.chanel);
            this.client.send(end);

            if (timers != null)
                timers.stopAllTimers();
            timers = null;
        }
        setState(ConnectionState.NONE);
    }

    public Message getPingreqMessage()
    {
        return new AMQPPing();
    }

    public void subscribe(String topicName, QoS qos) {

        Long currentHandler;
        if (this.usedIncomingMappings.containsKey(topicName)) {
            currentHandler = this.usedIncomingMappings.get(topicName);
        } else {
            currentHandler = this.nextHandle++;
            this.usedIncomingMappings.put(topicName, currentHandler);
            this.usedMappings.put(currentHandler, topicName);
        }

        AMQPAttach attach = new AMQPAttach();
        attach.setChannel(this.chanel);
        attach.setHandle(currentHandler);
        attach.setRole(RoleCodes.RECEIVER);
        attach.setSndSettleMode(SendCodes.MIXED);
        AMQPTarget target = new AMQPTarget();
        target.setAddress(topicName);
        target.setDurable(TerminusDurability.NONE);
        target.setTimeout(0L);
        target.setDynamic(false);
        attach.setTarget(target);
        client.send(attach);
    }

    public void unsubscribe(String topicName, QoS qos) {

        if (this.usedIncomingMappings.containsKey(topicName)) {
            Long currentHandler = this.usedIncomingMappings.get(topicName);
            AMQPDetach detach = new AMQPDetach();
            detach.setChannel(this.chanel);
            detach.setClosed(true);
            detach.setHandle(currentHandler);
            this.client.send(detach);
        } else {
            this.dbListener.deleteTopics(new Text(topicName));
            //sendMessageIntent(MessageType.UNSUBACK);
        }
    }

    public void publish(String topicName, QoS qos, byte[] content, boolean retain, boolean dup) {

        AMQPTransfer transfer = new AMQPTransfer();
        transfer.setChannel(this.chanel);
        transfer.setDeliveryId(0L);
        transfer.setSettled(false);
        transfer.setMore(false);
        transfer.setMessageFormat(new AMQPMessageFormat(0));

        MessageHeader messageHeader = new MessageHeader();
        messageHeader.setDurable(true);
        messageHeader.setPriority((short) 3);
        messageHeader.setMilliseconds(1000L);

        AMQPData data = new AMQPData();
        data.setValue(content);

        AMQPSection[] sections = { data };

        transfer.addSections(sections);

        if (this.usedOutgoingMappings.containsKey(topicName)) {
            Long handle = this.usedOutgoingMappings.get(topicName);
            transfer.setHandle(handle);
            this.timers.store(transfer);
            this.client.send(transfer);
        } else {
            Long currentHandler = this.nextHandle++;
            this.usedOutgoingMappings.put(topicName, currentHandler);
            this.usedMappings.put(currentHandler, topicName);

            transfer.setHandle(currentHandler);
            this.pendingMessages.add(transfer);

            AMQPAttach attach = new AMQPAttach();
            attach.setChannel(this.chanel);
            attach.setName(topicName);
            attach.setHandle(currentHandler);
            attach.setRole(RoleCodes.SENDER);
            attach.setSndSettleMode(SendCodes.MIXED);
            AMQPSource source = new AMQPSource();
            source.setAddress(topicName);
            source.setDurable(TerminusDurability.NONE);
            source.setTimeout(0L);
            source.setDynamic(false);
            attach.setSource(source);
            this.client.send(attach);
        }
    }

    public void reinit() {

        setState(ConnectionState.CHANNEL_CREATING);

        if (client != null)
            client.shutdown();

        client = new TCPClient(address, workerThreads);
    }

    public void closeConnection() {
        if (timers != null)
            timers.stopAllTimers();

        if (client != null) {
            TCPClient currClient = client;
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
            sendMessageIntent(HeaderCodes.FLOW);
        }
    }

    @Override
    public void processMessage(Message message) {

        if (message.getProtocol() != Protocols.AMQP_PROTOCOL) {
            return;
        }

        HeaderCodes type = HeaderCodes.valueOf(message.getType());

        switch (type) {
            case PROTO:
            {
                AMQPProtoHeader protoHeader = (AMQPProtoHeader)message;
                if (isSASLСonfirm && protoHeader.getProtocolId() == 0) {
                    chanel = protoHeader.getChannel();
                    AMQPOpen open = new AMQPOpen();
                    open.setContainerId(UUID.randomUUID().toString());
                    open.setChannel(protoHeader.getChannel());
                    client.send(open);
                } else {
                    this.timers.stopAllTimers();
                    setState(ConnectionState.CONNECTION_FAILED);
                }
            } break;
            case OPEN:
            {
                AMQPOpen open = (AMQPOpen)message;
                int idleTimeoutInSeconds = open.getIdleTimeout().intValue() / 1000;
                timers.startPingTimer(idleTimeoutInSeconds);

                AMQPBegin begin = new AMQPBegin();
                begin.setChannel(chanel);
                begin.setNextOutgoingId((long) 0);
                begin.setIncomingWindow((long) 2147483647);
                begin.setOutgoingWindow((long) 0);
                client.send(begin);
            } break;
            case BEGIN:
            {
                setState(ConnectionState.CONNECTION_ESTABLISHED);

                if (this.isClean) {
                    this.cleanCurrentSession();
                }
            } break;
            case ATTACH:
            {
                AMQPAttach attach = (AMQPAttach)message;

                if (attach.getRole() != null) {
                    if (attach.getRole() == RoleCodes.SENDER) {
                        // publish
                        if (attach.getHandle() != null) {
                            for (int i = 0; i < this.pendingMessages.size(); i++) {
                                AMQPTransfer currentMessage = this.pendingMessages.get(i);
                                if (currentMessage.getHandle().equals(attach.getHandle())) {
                                    pendingMessages.remove(i);
                                    i--;
                                    this.timers.store(currentMessage);
                                    this.client.send(currentMessage);
                                }
                            }
                        }
                    } else {
                        // subscribe
                    }
                }
            } break;
            case FLOW:
            {
//                // not implemented for now
//                AMQPFlow flow = (AMQPFlow)message;
//                if (chanel == flow.getChannel()) {
//                    isPublishAllow = true;
//                }
            } break;
            case TRANSFER:
            {
                AMQPTransfer transfer = (AMQPTransfer)message;

                QoS qos = QoS.AT_LEAST_ONCE;
                if (transfer.getSettled()) {
                    qos = QoS.AT_MOST_ONCE;
                } else {
                    AMQPDisposition disposition = new AMQPDisposition();
                    disposition.setChannel(this.chanel);
                    disposition.setRole(RoleCodes.RECEIVER);
                    disposition.setFirst(transfer.getDeliveryId());
                    disposition.setLast(transfer.getDeliveryId());
                    disposition.setSettled(true);
                    disposition.setState(new AMQPAccepted());
                    client.send(disposition);
                }

                String topicName;
                if (transfer.getHandle() == null || !this.usedMappings.containsKey(transfer.getHandle()))
                    return;

                topicName = this.usedMappings.get(transfer.getHandle());
                if (!dbListener.isTopicExist(topicName))
                    return;

                String content = new String(transfer.getData().getValue().getBytes());
                this.dbListener.addMessage(content, qos.getValue(), true, topicName);
                //sendMessageIntent(MessageType.PUBLISH);

            } break;
            case DISPOSITION:
            {
                AMQPDisposition disposition = (AMQPDisposition)message;

                Long first = disposition.getFirst();
                Long second = disposition.getLast();
                if (first != null && second != null) {
                    for (Long i = first; i < second; i++) {
                        this.timers.remove(i.intValue());
                    }
                }
            } break;
            case DETACH:
            {
                AMQPDetach detach = (AMQPDetach)message;
                if (detach.getHandle() != null && this.usedMappings.containsKey(detach.getHandle())) {
                    String topicName = this.usedMappings.get(detach.getHandle());
                    if (this.usedOutgoingMappings.containsKey(topicName)) {
                        this.usedOutgoingMappings.remove(topicName);
                    }
                }
            } break;
            case END:
            {
                AMQPEnd end = (AMQPEnd)message;
                AMQPClose close = new AMQPClose();
                close.setChannel(end.getChannel());
                client.send(close);
            } break;
            case CLOSE:
            {
                new Thread(new Runnable() {
                    public void run() {
                        if (client.isConnected()) {
                            client.close();
                            if (connectionCheckTask != null) {
                                connectionCheckTask.cancel();
                            }
                        }
                        setState(ConnectionState.NONE);
                        isSASLСonfirm = false;
                    }
                }).start();
            } break;
            case MECHANISMS:
            {
                SASLMechanisms item = (SASLMechanisms)message;

                AMQPSymbol plainMechanism = null;
                ArrayList<AMQPSymbol> mechanisms = new ArrayList<>(item.getMechanisms());
                for (int i = 0; i < mechanisms.size(); i++) {
                    if (mechanisms.get(i).getValue().toLowerCase().equals("plain")) {
                        plainMechanism = mechanisms.get(i);
                        break;
                    }
                }

                if (plainMechanism == null) {
                    this.timers.stopAllTimers();
                    setState(ConnectionState.CONNECTION_FAILED);
                    return;
                }

                SASLInit saslInit = new SASLInit();
                saslInit.setHeaderType(item.getHeaderType());
                saslInit.setChannel(item.getChannel());
                saslInit.setMechanism(plainMechanism.getValue());

                byte[] userBytes = this.username.getBytes();
                byte[] passwordBytes = this.password.getBytes();
                byte[] challenge = new byte[userBytes.length + 1 + userBytes.length + 1 + passwordBytes.length];

                System.arraycopy(userBytes, 0, challenge, 0, userBytes.length);
                challenge[userBytes.length] = 0x00;
                System.arraycopy(userBytes, 0, challenge, userBytes.length + 1, userBytes.length);
                challenge[userBytes.length + 1 + userBytes.length] = 0x00;
                System.arraycopy(passwordBytes, 0, challenge, userBytes.length + 1 + userBytes.length + 1, passwordBytes.length);

                saslInit.setInitialResponse(challenge);
                client.send(saslInit);
            } break;
            case INIT:
            {
                throw new CoreLogicException("received invalid message init");
            }
            case CHALLENGE:
            {
                throw new CoreLogicException("received invalid message challenge");
            }
            case RESPONSE:
            {
                throw new CoreLogicException("received invalid message response");
            }
            case OUTCOME:
            {
                SASLOutcome outcome = (SASLOutcome)message;
                if (outcome.getOutcomeCode() == OutcomeCodes.OK) {
                    this.isSASLСonfirm = true;
                    AMQPProtoHeader header = new AMQPProtoHeader(0);
                    client.send(header);
                } else if (outcome.getOutcomeCode() == OutcomeCodes.AUTH) {
                    throw new CoreLogicException("received invalid message outcome(AUTH)");
                } else if (outcome.getOutcomeCode() == OutcomeCodes.SYS) {
                    throw new CoreLogicException("received invalid message outcome(SYS)");
                } else if (outcome.getOutcomeCode() == OutcomeCodes.SYS_PERM) {
                    throw new CoreLogicException("received invalid message outcome(SYS_PERM)");
                } else if (outcome.getOutcomeCode() == OutcomeCodes.SYS_TEMP) {
                    throw new CoreLogicException("received invalid message outcome(SYS_TEMP)");
                }
            } break;
            case PING:
            {

            } break;
        }
    }

    public void sendMessageIntent(HeaderCodes messageType) {

        Intent startServiceIntent = new Intent(context, NetworkService.class);

        startServiceIntent.putExtra(ApplicationSettings.PARAM_MESSAGETYPE, Integer.toString(messageType.getType()));
        startServiceIntent.setAction(ApplicationSettings.ACTION_MESSAGE_RECEIVED);
        context.startService(startServiceIntent);
    }

    public void executeTimer(TimerTask task, long period) {
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
