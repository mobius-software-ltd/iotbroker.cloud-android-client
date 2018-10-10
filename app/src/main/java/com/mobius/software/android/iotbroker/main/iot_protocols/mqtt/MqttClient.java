package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt;

/**
 * Mobius Software LTD
 * Copyright 2015-2016, Mobius Software LTD
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

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mobius.software.android.iotbroker.main.CoreLogicException;
import com.mobius.software.android.iotbroker.main.base.ApplicationSettings;
import com.mobius.software.android.iotbroker.main.iot_protocols.IotProtocol;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.TimersMap;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.MQParser;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.Text;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Connack;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Pingreq;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Suback;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Unsuback;
import com.mobius.software.android.iotbroker.main.listeners.ClientStateListener;
import com.mobius.software.android.iotbroker.main.listeners.DataBaseListener;
import com.mobius.software.android.iotbroker.main.managers.ConnectionState;
import com.mobius.software.android.iotbroker.main.managers.ConnectionTimerTask;
import com.mobius.software.android.iotbroker.main.managers.MessageResendTimerTask;
import com.mobius.software.android.iotbroker.main.net.TCPClient;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.ConnackCode;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MessageType;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.QoS;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.SubackCode;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MQTopic;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.Will;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Connect;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Disconnect;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Puback;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Pubcomp;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Publish;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Pubrec;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Pubrel;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Subscribe;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Unsubscribe;
import com.mobius.software.android.iotbroker.main.services.NetworkService;
import com.mobius.software.android.iotbroker.main.utility.FileManager;

public class MqttClient implements IotProtocol {

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

	private DataBaseListener dbListener;
	private static ConnectionState currentState;

	public MqttClient(InetSocketAddress address, String username, String password, String clientID, boolean isClean,
			int keepalive, Will will, Context context) {
		this.address = address;
		this.username = username;
		this.password = password;
		this.clientID = clientID;
		this.isClean = isClean;
		this.keepalive = keepalive;
		this.will = will;
		this.context = context;
		client = new TCPClient(address, workerThreads);
		client.setSecure(false);
	}

	public MqttClient(InetSocketAddress address, String username, String password, String clientID, boolean isClean,
					  int keepalive, Will will, String crtPath, String crtPass, Context context) {
		this.address = address;
		this.username = username;
		this.password = password;
		this.clientID = clientID;
		this.isClean = isClean;
		this.keepalive = keepalive;
		this.will = will;
		this.context = context;
		client = new TCPClient(address, workerThreads);
		client.setSecure(true);
		client.setKeyStore(FileManager.loadKeyStore(crtPath, crtPass));
		client.setKeyStorePassword(crtPass);
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
		Boolean isSuccess = client.init(this, new MQParser());
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
		Connect connect = new Connect(username, password, clientID, isClean, keepalive, will);

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
			if (timers != null) {
				timers.stopAllTimers();
				timers = null;
			}

			client.send(new Disconnect());
			client.close();

			if (connectionCheckTask != null) {
				connectionCheckTask.cancel();
			}

		}
	}

	public Message getPingreqMessage()
	{
		return new Pingreq();
	}

	public void subscribe(String topicName, QoS qos) {

        MQTopic topic = new MQTopic(new Text(topicName), qos);
        MQTopic[] topics = new MQTopic[] { topic };

        Subscribe subscribe = new Subscribe(topics);
        timers.store(subscribe);
        client.send(subscribe);
    }

    public void unsubscribe(String topicName, QoS qos) {

		Text[] topics = new Text[] { new Text(topicName) };

        Unsubscribe uunsubscribe = new Unsubscribe(topics);
        timers.store(uunsubscribe);
        client.send(uunsubscribe);
    }

    public void publish(String topicName, QoS qos, byte[] content, boolean retain, boolean dup) {

        MQTopic topic = new MQTopic(new Text(topicName), qos);

        Publish publish = new Publish(topic, content, retain, dup);
        if (topic.getQos() != QoS.AT_MOST_ONCE)
            timers.store(publish);

        client.send(publish);
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
			sendMessageIntent(MessageType.SUBACK);
		}
	}

	@Override
	public void processMessage(Message message) {

		if (message.getProtocol() != Protocols.MQTT_PROTOCOL) {
			return;
		}

        MessageType type = MessageType.valueOf(message.getType());

		switch (type) {
			case CONNECT:
            {
                throw new CoreLogicException("received invalid message connect");
            }
            case CONNACK:
			{
				Connack connack = (Connack)message;
				MessageResendTimerTask timer = timers.stopConnectTimer();

				if (connack.getReturnCode() == ConnackCode.ACCEPTED) {
					setState(ConnectionState.CONNECTION_ESTABLISHED);

					if (timer != null) {
						Connect connect = (Connect) timer.retrieveMessage();
						if (connect.isCleanSession()) {
							cleanCurrentSession();
						}
					}

					timers.startPingTimer(keepalive);

				}
				else {
					timers.stopAllTimers();
					client.shutdown();
					setState(ConnectionState.CHANNEL_FAILED);
				}
			} break;
			case PUBLISH:
			{
				Publish publish = (Publish)message;
				QoS publisherQos = publish.getTopic().getQos();
				switch (publisherQos) {
					case AT_LEAST_ONCE:
						Puback puback = new Puback(publish.getPacketID());
						client.send(puback);
						break;
					case EXACTLY_ONCE:
						Pubrec pubrec = new Pubrec(publish.getPacketID());
						client.send(pubrec);
						break;
					default:
						break;
				}

				if (!(publish.isDup() && publisherQos == QoS.EXACTLY_ONCE)) {
					String contentMessage;
					try {
						contentMessage = new String(publish.getContent(), "UTF-8");
						dbListener.addMessage(contentMessage, publisherQos.getValue(), true, publish.getTopic().getName().toString());
					} catch (UnsupportedEncodingException e) {

					}
				}
			} break;
			case PUBACK:
			{
				Puback puback = (Puback)message;
				timers.remove(puback.getPacketID());
				sendMessageIntent(MessageType.PUBACK);
			} break;
			case PUBREC:
			{
				Pubrec pubrec = (Pubrec)message;
				Pubrel pubrel = new Pubrel(pubrec.getPacketID());
				timers.refreshTimer(pubrel);
				client.send(pubrel);
			} break;
			case PUBREL:
			{
				Pubrel pubrel = (Pubrel)message;
				client.send(new Pubcomp(pubrel.getPacketID()));
			} break;
			case PUBCOMP:
			{
				Pubcomp pubcomp = (Pubcomp)message;
				timers.remove(pubcomp.getPacketID());
				sendMessageIntent(MessageType.PUBCOMP);
			} break;
			case SUBSCRIBE:
			{
				throw new CoreLogicException("received invalid message subscribe");
			}
			case SUBACK:
			{
				Suback suback = (Suback)message;
				MessageResendTimerTask timer = timers.remove(suback.getPacketID());

				for (SubackCode code : suback.getReturnCodes()) {
					if (code == SubackCode.FAILURE) {
						throw new CoreLogicException("received invalid message suback");
					}
					else {
						Subscribe subscribe = (Subscribe) timer.retrieveMessage();
						MQTopic topic = subscribe.getTopics()[0];
						QoS expectedQos = topic.getQos();
						QoS actualQos = QoS.valueOf(code.getNum());
						if (expectedQos == actualQos) {
							int qos = expectedQos.getValue();
							writeTopics(topic.getName().toString(), qos);
						}
						else {
							int qos = expectedQos.getValue();
							writeTopics(topic.getName().toString(), qos);
						}
					}
				}
			} break;
			case UNSUBSCRIBE:
			{
				throw new CoreLogicException("received invalid message unsubscribe");
			}
			case UNSUBACK:
			{
				Unsuback unsuback = (Unsuback)message;
				MessageResendTimerTask timer = timers.remove(unsuback.getPacketID());

				if (timer != null) {
					Unsubscribe unsubscribe = (Unsubscribe) timer.retrieveMessage();
					Text[] topics = unsubscribe.getTopics();

					for (Text topic : topics) {
						dbListener.deleteTopics(topic);
					}
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
				throw new CoreLogicException("received invalid message disconnect");
			}
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
	public void timeout() {

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