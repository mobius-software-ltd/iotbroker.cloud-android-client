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
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.MessageHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.terminus.AMQPSource;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.terminus.AMQPTarget;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described.AMQPAccepted;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.AMQPMessageFormat;
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
import java.net.InetSocketAddress;
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

    private AMQPTransferMap transferMap;
    private boolean isSASLСonfirm;
    private boolean isPublishAllow;
    private int chanel;

    private DataBaseListener dbListener;
    private static ConnectionState currentState;

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
        this.transferMap = new AMQPTransferMap();
        this.isSASLСonfirm = false;
        this.isPublishAllow = false;
        this.chanel = 0;
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
        this.transferMap = new AMQPTransferMap();
        this.isSASLСonfirm = false;
        this.isPublishAllow = false;
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
            AMQPDetach detach = new AMQPDetach();
            detach.setChannel(chanel);
            detach.setHandle((long) 1);
            detach.setClosed(true);

            client.send(detach);

            if (timers != null)
                timers.stopAllTimers();
            timers = null;
        }
    }

    public Message getPingreqMessage()
    {
        return new AMQPPing();
    }

    public void subscribe(String topicName, QoS qos) {

        AMQPFlow flow = new AMQPFlow();
        flow.setChannel(chanel);
        flow.setHandle((long) 1);
        flow.setIncomingWindow((long) 65535);
        flow.setOutgoingWindow((long) 65535);
        flow.setLinkCredit((long) 65535);
        flow.setNextOutgoingId((long) 0);
        flow.setNextIncomingId((long) 0);
        flow.setDrain(false);
        flow.setEcho(false);

        client.send(flow);
    }

    public void unsubscribe(String topicName, QoS qos) {
    }

    public void publish(String topicName, QoS qos, byte[] content, boolean retain, boolean dup) {

        if (isPublishAllow == true) {
            AMQPTransfer transfer = new AMQPTransfer();
            transfer.setChannel(chanel);
            transfer.setHandle((long) 0);
            transfer.setDeliveryId((long) 0);
            transfer.setSettled(false);
            transfer.setMore(false);
            transfer.setMessageFormat(new AMQPMessageFormat(0));

            MessageHeader messageHeader = new MessageHeader();
            messageHeader.setDurable(true);
            messageHeader.setPriority((short) 3);
            messageHeader.setMilliseconds((long) 1000);

            AMQPData data = new AMQPData();
            data.setValue(content);

            transfer.addSections(data);

            client.send(transferMap.addTransfer(transfer));
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
            clearAccountTopics();
        setState(ConnectionState.CONNECTION_LOST);

        if (timers != null)
            timers.stopAllTimers();
    }

    private void clearAccountTopics() {
        dbListener.clearTopicByActiveAccount();
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
                AMQPAttach attach = new AMQPAttach();
                attach.setChannel(chanel);
                attach.setName(UUID.randomUUID().toString());
                attach.setHandle((long) 0);
                attach.setRole(RoleCodes.SENDER);
                attach.setSndSettleMode(SendCodes.MIXED);
                AMQPSource source = new AMQPSource();
                source.setAddress("my_queue");
                source.setDurable(TerminusDurability.NONE);
                source.setTimeout((long) 0);
                source.setDynamic(false);
                attach.setSource(source);
                AMQPTarget target = new AMQPTarget();
                target.setAddress("my_queue");
                target.setDurable(TerminusDurability.NONE);
                target.setTimeout((long) 0);
                target.setDynamic(false);
                attach.setTarget(target);
                attach.setInitialDeliveryCount((long) 1);
                client.send(attach);
            } break;
            case ATTACH:
            {
                AMQPAttach attachMsg = (AMQPAttach)message;

                if (attachMsg.getRole() == RoleCodes.SENDER) {
                    setState(ConnectionState.CONNECTION_ESTABLISHED);

                    if (timer != null) {
                        if (isClean) {
                            clearAccountTopics();
                        }
                    }
                } else {
                    AMQPAttach attach = new AMQPAttach();
                    attach.setChannel(chanel);
                    attach.setName(UUID.randomUUID().toString());
                    attach.setHandle((long) 1);
                    attach.setRole(RoleCodes.RECEIVER);
                    attach.setSndSettleMode(SendCodes.MIXED);
                    AMQPSource source = new AMQPSource();
                    source.setAddress("my_queue");
                    source.setDurable(TerminusDurability.NONE);
                    source.setTimeout((long) 0);
                    source.setDynamic(false);
                    attach.setSource(source);
                    AMQPTarget target = new AMQPTarget();
                    target.setAddress("my_queue");
                    target.setDurable(TerminusDurability.NONE);
                    target.setTimeout((long) 0);
                    target.setDynamic(false);
                    attach.setTarget(target);
                    attach.setInitialDeliveryCount((long) 1);
                    client.send(attach);
                }
            } break;
            case FLOW:
            {
                AMQPFlow flow = (AMQPFlow)message;
                if (chanel == flow.getChannel()) {
                    isPublishAllow = true;
                }
            } break;
            case TRANSFER:
            {
                AMQPTransfer transfer = (AMQPTransfer)message;
                AMQPData data = (AMQPData)transfer.getData();
                int qos;

                if (transfer.getSettled()) {
                    qos = 0;
                } else {
                    qos = 1;

                    AMQPDisposition disposition = new AMQPDisposition();
                    disposition.setChannel(chanel);
                    disposition.setRole(RoleCodes.RECEIVER);
                    disposition.setFirst(transfer.getDeliveryId());
                    disposition.setLast(transfer.getDeliveryId());
                    disposition.setSettled(true);
                    disposition.setState(new AMQPAccepted());
                    client.send(disposition);
                }

                String contentMessage = null;
                try {
                    contentMessage = new String(data.getData(), "UTF-8");
                    dbListener.addMessage(contentMessage, qos, true, "");
                }
                catch (UnsupportedEncodingException e) {

                }
            } break;
            case DISPOSITION:
            {
                AMQPDisposition disposition = (AMQPDisposition)message;

                int first = disposition.getFirst().intValue();
                int second = disposition.getLast().intValue();

                for (int i = first; i <= second; i++) {
                    AMQPTransfer transfer = transferMap.removeTransfer(i);
                    AMQPData data = (AMQPData)transfer.getData();
                    sendMessageIntent(HeaderCodes.DISPOSITION);
                }
            } break;
            case DETACH:
            {
                AMQPDetach detach = (AMQPDetach)message;
                AMQPEnd end = new AMQPEnd();
                end.setChannel(detach.getChannel());
                client.send(end);
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
                        client.close();
                        if (connectionCheckTask != null) {
                            connectionCheckTask.cancel();
                        }
                    }
                }).start();
                //timers.stopAllTimers();
                isSASLСonfirm = false;
            } break;
            case MECHANISMS:
            {
                SASLMechanisms mechanisms = (SASLMechanisms)message;

                SASLInit saslInit = new SASLInit();
                saslInit.setHeaderType(mechanisms.getHeaderType());
                saslInit.setChannel(mechanisms.getChannel());
                saslInit.setMechanism(mechanisms.getMechanisms().get(0).toString());

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
