package com.mobius.software.android.iotbroker.mqtt.services;

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

import java.net.InetSocketAddress;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

import com.mobius.software.android.iotbroker.mqtt.MqttClient;
import com.mobius.software.android.iotbroker.mqtt.listeners.ClientStateListener;
import com.mobius.software.android.iotbroker.mqtt.listeners.NetworkStateListener;
import com.mobius.software.android.iotbroker.mqtt.managers.AppBroadcastManager;
import com.mobius.software.android.iotbroker.mqtt.managers.ConnectionState;
import com.mobius.software.android.iotbroker.mqtt.managers.NetworkManager;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.QoS;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.Text;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.Topic;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.Will;

public class NetworkService extends Service implements NetworkStateListener,ClientStateListener {

	private static NetworkService instance = null;
	private static MqttClient client = null;
	
	@Override
	public IBinder onBind(Intent intent) 
	{
		return null;
	}

	@SuppressLint("InlinedApi")
	@Override
	public void onCreate() {
		if (instance == null) {
			instance = this;

			NetworkManager.updateNetworkInfo(this);
			NetworkManager.setNetworkListener(this);

			if (client != null) {
				client.setListener(instance);
				this.stateChanged(client.getConnectionState());
			}
		}
	}

	public static Context getCurrentContext() {
		return instance;
	}

	public static boolean hasInstance() {
		return instance != null;
	}

	public static Boolean reactivate() {
		client.reinit();
		return client.createChannel();		
	}

	public static Boolean activateService(InetSocketAddress address,String username, String password, String clientID, boolean isClean,int keepalive, Will will) 
	{
		if (client != null) 
		{
			client.closeConnection();
			client = null;
		}

		Context context = getCurrentContext();
		client = new MqttClient(address, username, password, clientID, isClean,keepalive, will, context);
		client.setListener(instance);
		return client.createChannel();		
	}

	public static void deActivateService() {
		if (client != null) {
			if (client.checkConnected())
				client.disconnect();

			client = null;
		}
	}

	public static ConnectionState getStatus() {
		if (client == null)
			return ConnectionState.NONE;

		return client.getConnectionState();
	}

	@Override
	public void onDestroy() {
		deActivateService();
		instance = null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void executeAsyncTask(AsyncTask task, Object... strings) {
		task.execute(strings);
	}

	public static void subscribe(String topicName, int qos) {
		if (client != null && client.checkConnected()) {
			QoS qosItem = QoS.valueOf(qos);
			Text topicNameTxt = new Text(topicName);
			Topic topic = new Topic(topicNameTxt, qosItem);

			Topic[] topics = new Topic[] { topic };
			client.subscribe(topics);
		}
	}

	public static void unsubscribe(String topicName, int qos) {
		if (client != null && client.checkConnected()) {
			QoS qosItem = QoS.valueOf(qos);
			Text topicNameTxt = new Text(topicName);
			Topic topic = new Topic(topicNameTxt, qosItem);

			Topic[] topics = new Topic[] { topic };
			client.unsubscribe(topics);
		}
	}

	public static void publish(String content, String topicName, int qos,String packedId, boolean isRetain, boolean isDublicate) 
	{
		if (client != null && client.checkConnected()) 
		{
			QoS qosItem = QoS.valueOf(qos);
			Text topicNameTxt = new Text(topicName);
			Topic topic = new Topic(topicNameTxt, qosItem);

			client.publish(topic, content.getBytes(), isRetain, isDublicate);
		}
	}

	@Override
	public void networkUp() {
		Intent addIntent = new Intent();
		addIntent.setAction(AppBroadcastManager.NETWORK_UP);
		instance.getApplicationContext().sendBroadcast(addIntent);
	}

	@Override
	public void networkDown() {
		deActivateService();
		Intent addIntent = new Intent();
		addIntent.setAction(AppBroadcastManager.NETWORK_DOWN);
		instance.getApplicationContext().sendBroadcast(addIntent);
	}

	@Override
	public void networkChanged() {
		deActivateService();
		Intent addIntent = new Intent();
		addIntent.setAction(AppBroadcastManager.NETWORK_CHANGED);
		instance.getApplicationContext().sendBroadcast(addIntent);
	}

	public void writeError() {
		deActivateService();
		if (instance != null) {
			Intent addIntent = new Intent();
			addIntent.setAction(AppBroadcastManager.NETWORK_DOWN);
			instance.getApplicationContext().sendBroadcast(addIntent);
		}
	}

	@Override
	public void stateChanged(ConnectionState newState) {
		Intent addIntent = new Intent();
		addIntent.putExtra("status", newState.toString());
		addIntent.setAction(AppBroadcastManager.NETWORK_STATUS_CHANGE);
		instance.getApplicationContext().sendBroadcast(addIntent);

		switch (client.getConnectionState()) {
		case CHANNEL_ESTABLISHED:
			if (client.checkCreated())
				client.connect();
			break;
		case CONNECTION_LOST:
			client.closeConnection();
		default:
			break;
		}		
	}	
}