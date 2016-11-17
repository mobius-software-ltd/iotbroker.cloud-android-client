package com.mobius.software.android.iotbroker.mqtt;

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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;

import com.mobius.software.android.iotbroker.mqtt.dal.AccountDAO;
import com.mobius.software.android.iotbroker.mqtt.dal.AccountManager;
import com.mobius.software.android.iotbroker.mqtt.dal.MessagesManager;
import com.mobius.software.android.iotbroker.mqtt.dal.TopicDAO;
import com.mobius.software.android.iotbroker.mqtt.dal.TopicsManager;
import com.mobius.software.android.iotbroker.mqtt.listeners.ClientStateListener;
import com.mobius.software.android.iotbroker.mqtt.listeners.ConnectionListener;
import com.mobius.software.android.iotbroker.mqtt.managers.AppBroadcastManager;
import com.mobius.software.android.iotbroker.mqtt.managers.ConnectResendTimerTask;
import com.mobius.software.android.iotbroker.mqtt.managers.ConnectionState;
import com.mobius.software.android.iotbroker.mqtt.managers.ConnectionTimerTask;
import com.mobius.software.android.iotbroker.mqtt.managers.MessageResendTimerTask;
import com.mobius.software.android.iotbroker.mqtt.net.TCPClient;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.ConnackCode;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.MessageType;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.QoS;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.SubackCode;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.Topic;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.Will;
import com.mobius.software.android.iotbroker.mqtt.parser.header.api.MQDevice;
import com.mobius.software.android.iotbroker.mqtt.parser.header.api.MQMessage;
import com.mobius.software.android.iotbroker.mqtt.parser.header.impl.Connect;
import com.mobius.software.android.iotbroker.mqtt.parser.header.impl.Disconnect;
import com.mobius.software.android.iotbroker.mqtt.parser.header.impl.Puback;
import com.mobius.software.android.iotbroker.mqtt.parser.header.impl.Pubcomp;
import com.mobius.software.android.iotbroker.mqtt.parser.header.impl.Publish;
import com.mobius.software.android.iotbroker.mqtt.parser.header.impl.Pubrec;
import com.mobius.software.android.iotbroker.mqtt.parser.header.impl.Pubrel;
import com.mobius.software.android.iotbroker.mqtt.parser.header.impl.Subscribe;
import com.mobius.software.android.iotbroker.mqtt.parser.header.impl.Unsubscribe;

public class MqttClient implements MQDevice, ConnectionListener {

	public final static String MESSAGETYPE_PARAM = "MESSAGETYPE";

	public static final int SERVER_PORT = 1883;
	private Integer workerThreads = 4;
	private final long PERIOD = 3000;

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
	
	public MqttClient(InetSocketAddress address, String username,
			String password, String clientID, boolean isClean, int keepalive,
			Will will, Context context) {

		this.address = address;
		this.username = username;
		this.password = password;
		this.clientID = clientID;
		this.isClean = isClean;
		this.keepalive = keepalive;
		this.will = will;
		this.context = context;
		client = new TCPClient(address, workerThreads);
	}

	public void setListener(ClientStateListener listener)
	{
		this.listener=listener;
	}
	
	private void setState(ConnectionState state)
	{
		this.connectionState=state;
		if(this.listener!=null)
			listener.stateChanged(state);
	}
	
	public Boolean createChannel() {
		setState(ConnectionState.CHANNEL_CREATING);		
		Boolean isSuccess = client.init(this);
		if (!isSuccess)
			setState(ConnectionState.CHANNEL_FAILED);
		else
		{
			ConnectionTimerTask connectionCheckTask = new ConnectionTimerTask(this);
			executeTimer(connectionCheckTask,ConnectionTimerTask.REFRESH_PERIOD);
		}
		
		return isSuccess;
	}

	public boolean checkCreated() {
		Boolean isConnected=client.isConnected();
		if(isConnected && connectionState==ConnectionState.CHANNEL_CREATING)
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
		Connect connect = new Connect(username, password, clientID, isClean,
				keepalive, will);

		if (timers != null)
			timers.stopAllTimers();

		timers = new TimersMap(this, client, PERIOD);
		timers.storeConnectTimer(connect);

		if (client.isConnected()) {
			client.send(connect);
		}
	}

	public void disconnect() {
		if (client.isConnected()) {
			client.send(new Disconnect());
			client.close();
		}

		setState(ConnectionState.NONE);
		return;
	}

	public void subscribe(Topic[] topics) {
		Subscribe subscribe = new Subscribe(topics);
		timers.store(subscribe);
		client.send(subscribe);
	}

	public void unsubscribe(Topic[] topics) {
		Unsubscribe uunsubscribe = new Unsubscribe(topics);
		timers.store(uunsubscribe);
		client.send(uunsubscribe);
	}

	public void publish(Topic topic, byte[] content, boolean retain, boolean dup) {
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
	public void packetReceived(MQMessage message) {
		try {
			message.processBy(this);
		} catch (Exception e) {
			e.printStackTrace();
			client.shutdown();
		}
	}

	@Override
	public void connectionLost() {
		if (isClean)
			clearAccountTopics();
		setState(ConnectionState.CONNECTION_LOST);		
	}

	@Override
	public void processConnack(ConnackCode code, boolean sessionPresent) {
		// CANCEL CONNECT TIMER
		ConnectResendTimerTask timer = timers.stopConnectTimer();

		// CHECK CODE , IF OK THEN MOVE TO CONNECTED AND NOTIFY NETWORK SESSION
		if (code == ConnackCode.ACCEPTED) {
			setState(ConnectionState.CONNECTION_ESTABLISHED);

			if (timer != null) {
				Connect connect = (Connect) timer.retrieveMessage();
				if (connect.isClean()) {
					clearAccountTopics();
				}
			}

			timers.startPingTimer(keepalive);
			// /NetworkService.updateStatus(connectionState);

		} else {
			// OTHERWISE MOVE TO CONNECT FAILED, STOP CLIENT AND NOTIFY NETWORK
			// SESION
			timers.stopAllTimers();
			client.shutdown();
			setState(ConnectionState.CHANNEL_FAILED);
		}
	}

	private void clearAccountTopics() {
		AccountDAO currAccount = retrieveAccount();
		TopicsManager manager = new TopicsManager(context);
		manager.deleteByAccountId(currAccount.getId());
	}

	private AccountDAO retrieveAccount() {
		AccountManager accountMngr = new AccountManager(context);
		accountMngr.open();
		AccountDAO currAccount = accountMngr.getCurrentAccount();
		accountMngr.close();
		return currAccount;
	}

	@Override
	public void processSuback(Integer packetID, List<SubackCode> codes) {

		MessageResendTimerTask timer = timers.remove(packetID);

		for (SubackCode code : codes) {
			if (code == SubackCode.FAILURE) {
				throw new CoreLogicException("received invalid message suback");
			} else {
				Subscribe subscribe = (Subscribe) timer.retrieveMessage();
				Topic topic = subscribe.getTopics()[0];
				QoS expectedQos = topic.getQos();
				QoS actualQos = QoS.valueOf(code.getNum());
				if (expectedQos == actualQos) {
					int qos = expectedQos.getValue();
					writeTopics(topic.getName().toString(), qos);

				} else {

					int qos = expectedQos.getValue();
					writeTopics(topic.getName().toString(), qos);
				}
			}
		}
	}

	private void writeTopics(String topicName, int qos) {

		TopicsManager manager = new TopicsManager(context);

		AccountDAO currAccount = retrieveAccount();
		TopicDAO oldTopic = manager.getByName(topicName, currAccount.getId());
		if (oldTopic != null)
			manager.update(oldTopic.getId(), topicName, qos,
					currAccount.getId());
		else
			manager.insert(topicName, qos, currAccount.getId());

		sendMessageIntent(MessageType.SUBACK);

	}

	@Override
	public void processUnsuback(Integer packetID) {
		MessageResendTimerTask timer = timers.remove(packetID);

		TopicsManager manager = new TopicsManager(context);
		if (timer != null) {
			Unsubscribe unsubscribe = (Unsubscribe) timer.retrieveMessage();
			Topic[] topics = unsubscribe.getTopics();
			for (Topic topic : topics)
				manager.deleteByName(topic.getName(), retrieveAccount().getId());
		}

		sendMessageIntent(MessageType.UNSUBACK);
	}

	@Override
	public void processPublish(Integer packetID, Topic topic, byte[] content,
			boolean retain, boolean isDup) {
		QoS publisherQos = topic.getQos();
		switch (publisherQos) {
		case AT_LEAST_ONCE:
			Puback puback = new Puback(packetID);
			client.send(puback);
			break;
		case EXACTLY_ONCE:
			Pubrec pubrec = new Pubrec(packetID);
			client.send(pubrec);
			break;
		default:
			break;
		}

		sendMessageIntent(MessageType.PUBLISH);

		TopicsManager manager = new TopicsManager(context);
		String topicName = topic.getName().toString();

		AccountDAO account = retrieveAccount();
		if (!manager.isTopicExist(topicName, account.getId()))
			return;

		MessagesManager mesManager = new MessagesManager(context);
		int qos = publisherQos.getValue();

		if (!(isDup && publisherQos == QoS.EXACTLY_ONCE)) {
			String contentMessage = null;
			try {
				contentMessage = new String(content, "UTF-8");
				mesManager.insert(topicName, contentMessage, qos,
						retrieveAccount().getId());

			} catch (UnsupportedEncodingException e) {

			}
		}
	}

	@Override
	public void processPuback(Integer packetID) {
		timers.remove(packetID);
		sendMessageIntent(MessageType.PUBACK);
	}

	@Override
	public void processPubrec(Integer packetID) {
		MQMessage message = new Pubrel(packetID);
		timers.refreshTimer(message);
		client.send(message);
	}

	@Override
	public void processPubrel(Integer packetID) {
		client.send(new Pubcomp(packetID));
	}

	@Override
	public void processPubcomp(Integer packetID) {
		timers.remove(packetID);
		sendMessageIntent(MessageType.PUBCOMP);

	}

	@Override
	public void processPingresp() {

	}

	@Override
	public void processSubscribe(Integer packetID, Topic[] topics) {
		throw new CoreLogicException("received invalid message subscribe");
	}

	@Override
	public void processConnect(boolean cleanSession, int keepalive, Will will) {
		throw new CoreLogicException("received invalid message connect");
	}

	@Override
	public void processPingreq() {
		throw new CoreLogicException("received invalid message pingreq");
	}

	@Override
	public void processDisconnect() {
		throw new CoreLogicException("received invalid message disconnect");
	}

	@Override
	public void processUnsubscribe(Integer packetID, Topic[] topics) {
		throw new CoreLogicException("received invalid message unsubscribe");
	}

	public void sendMessageIntent(MessageType messageType) {
		Intent intent = new Intent();
		intent.putExtra(MESSAGETYPE_PARAM, messageType);
		intent.setAction(AppBroadcastManager.MESSAGE_STATUS_UPDATED);

		context.sendBroadcast(intent);
	}
	
	public void executeTimer(TimerTask task, long period) 
	{
		timer.schedule(task, period);
	}

	@Override
	public void writeError() 
	{
		if(this.listener!=null)
			listener.writeError();
	}	
}