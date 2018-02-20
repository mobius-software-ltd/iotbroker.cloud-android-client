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
import com.mobius.software.android.iotbroker.main.net.TCPClient;
import com.mobius.software.android.iotbroker.main.net.UDPClient;
import com.mobius.software.android.iotbroker.main.services.NetworkService;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
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
    private UDPClient client;
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

    private DataBaseListener dbListener;
    private static ConnectionState currentState;

    public CoapClient(InetSocketAddress address, String username, String password, String clientID, boolean isClean,
                      int keepalive, Will will, Context context) {

        this.address = address;
        this.username = username;
        this.password = password;
        this.clientID = clientID;
        this.isClean = isClean;
        this.keepalive = keepalive;
        this.will = will;
        this.context = context;
        client = new UDPClient(address, workerThreads);
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

    public void connect() {
        setState(client.isConnected() ? ConnectionState.CONNECTION_ESTABLISHED : ConnectionState.CONNECTION_FAILED);

        if (timers != null) {
            timers.stopAllTimers();
            timers = null;
        }

        if (timers == null) {
            timers = new TimersMap(this, client);
            timers.startPingTimer(keepalive);
        }
    }

    public void disconnect() {

        if (client.isConnected()) {
            if (timers != null) {
                timers.stopAllTimers();
                timers = null;
            }
        }
    }

    public Message getPingreqMessage()
    {
        return new CoapHeader(CoapCode.PUT, true, false, "");
    }

    public void subscribe(String topicName, QoS qos) {

        CoapHeader coapMessage = new CoapHeader(CoapCode.GET, true, true, "");
        coapMessage.addOption(CoapOptionType.OBSERVE, Integer.toString(0));
        coapMessage.addOption(CoapOptionType.URI_PATH, topicName);
        coapMessage.setCoapType(CoapType.CONFIRMABLE);
        client.send(coapMessage);
    }

    public void unsubscribe(String topicName, QoS qos) {

        CoapHeader coapMessage = new CoapHeader(CoapCode.GET, true, true, "");
        coapMessage.addOption(CoapOptionType.OBSERVE, Integer.toString(1));
        coapMessage.addOption(CoapOptionType.URI_PATH, topicName);
        coapMessage.setCoapType(CoapType.CONFIRMABLE);
        client.send(coapMessage);
    }

    public void publish(String topicName, QoS qos, byte[] content, boolean retain, boolean dup) {

        CoapHeader header = new CoapHeader(CoapCode.PUT, true, true, new String(content));
        header.addOption(CoapOptionType.URI_PATH, topicName);
        client.send(header);
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

        if (message.getType() == CoapCode.POST.getType() || message.getType() == CoapCode.PUT.getType()) {
            List<String> topicsArray = message.getOptions().get(CoapOptionType.URI_PATH);
            if (topicsArray != null && topicsArray.size() > 0) {
                String topic = topicsArray.get(0);
                byte[] content = message.getPayload().getBytes();

                try {
                    String contentMessage = new String(content, "UTF-8");
                    dbListener.addMessage(contentMessage, 0, true, topic);
                } catch (UnsupportedEncodingException e) {

                }

                CoapHeader ack = new CoapHeader(CoapCode.METHOD_NOT_ALLOWED, false, true, "");
                ack.addOption(CoapOptionType.CONTENT_FORMAT, "text/plain");
                ack.setCoapType(CoapType.ACKNOWLEDGEMENT);
                ack.setMessageID(message.getMessageID());
                ack.setToken(message.getToken());
                client.send(ack);
            }
        }

        switch (type) {
            case CONFIRMABLE:
            {
                message.setCoapType(CoapType.ACKNOWLEDGEMENT);
                client.send(message);
            } break;
            case NON_CONFIRMABLE:
            {
                timers.remove(message.getToken());
            } break;
            case ACKNOWLEDGEMENT:
            {
                if (message.getCode() == CoapCode.CONTENT) {
                    List<String> topicsArray = message.getOptions().get(CoapOptionType.URI_PATH);
                    if (topicsArray.size() > 0) {
                        String topic = topicsArray.get(0);
                        byte[] content = message.getPayload().getBytes();
                        try {
                            String contentMessage = new String(content, "UTF-8");
                            dbListener.addMessage(contentMessage, 0, true, topic);
                        } catch (UnsupportedEncodingException e) {

                        }
                    }
                }
                if (message.getCode() == CoapCode.GET) {
                    List<String> observeOptionValues = message.getOptions().get(CoapOptionType.OBSERVE);
                    if (observeOptionValues.size() > 0) {
                        int value = Integer.parseInt(observeOptionValues.get(0));
                        if (value == 0) {
                            sendMessageIntent(MessageType.SUBACK);
                        } else if (value == 1) {
                            sendMessageIntent(MessageType.UNSUBACK);
                        }
                    }
                } else {
                    List<String> topicsArray = message.getOptions().get(CoapOptionType.URI_PATH);
                    if (topicsArray != null && topicsArray.size() > 0) {
                        //String topic = topicsArray.get(0);
                        //byte[] content = ack.getPayload().getBytes();
                        sendMessageIntent(MessageType.PUBACK);
                    }
                }
            } break;
            case RESET:
            {
                timers.remove(message.getToken());
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
