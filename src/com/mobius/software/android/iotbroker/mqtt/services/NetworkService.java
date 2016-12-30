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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.mobius.software.android.iotbroker.mqtt.MqttClient;
import com.mobius.software.android.iotbroker.mqtt.activity.LoadingActivity;
import com.mobius.software.android.iotbroker.mqtt.base.ApplicationSettings;
import com.mobius.software.android.iotbroker.mqtt.base.WillParcel;
import com.mobius.software.android.iotbroker.mqtt.dal.DataBaseManager;
import com.mobius.software.android.iotbroker.mqtt.listeners.ClientStateListener;
import com.mobius.software.android.iotbroker.mqtt.listeners.NetworkStateListener;
import com.mobius.software.android.iotbroker.mqtt.managers.ConnectionState;
import com.mobius.software.android.iotbroker.mqtt.managers.NetworkManager;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.MessageType;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.QoS;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.Text;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.Topic;
import com.mobius.software.android.iotbroker.mqtt.parser.avps.Will;
import com.mobius.software.iotbroker.androidclient.R;

public class NetworkService extends Service implements NetworkStateListener, ClientStateListener {

	private static NetworkService instance = null;
	private static MqttClient client = null;
	private static DataBaseManager dbManager = null;

	private boolean tmActivityVisible;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressLint("InlinedApi")
	@Override
	public void onCreate() {
		if (instance == null) {
			instance = this;

			NetworkManager.updateNetworkInfo(this);
			NetworkManager.setNetworkListener(this);

			if (dbManager == null) {
				dbManager = new DataBaseManager(instance.getBaseContext());
			}
		}
		tmActivityVisible = false;

		if (client != null) {

			client.setListener(instance);
			client.setDbListener(dbManager);

			this.stateChanged(client.getConnectionState());
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		String action = intent.getAction();
		if (action != null) {
			if (action.equalsIgnoreCase(ApplicationSettings.ACTION_DEACTIVATE_SERVICE)) {
				deActivateService();
			}
			else if (action.equalsIgnoreCase(ApplicationSettings.ACTION_REACTIVATE_SERVICE)) {
				reactivate();
			}

			else if (action.equalsIgnoreCase(ApplicationSettings.ACTION_CHANNEL_CREATING)) {
				boolean res = false;
				String userName = intent.getStringExtra(ApplicationSettings.PARAM_USERNAME);
				String serverHost = intent.getStringExtra(ApplicationSettings.PARAM_SERVER_HOST);

				String userPass = intent.getStringExtra(ApplicationSettings.PARAM_PASSWORD);
				String clientID = intent.getStringExtra(ApplicationSettings.PARAM_CLIENTID);

				Boolean isCleanSession = Boolean.parseBoolean(intent
						.getStringExtra(ApplicationSettings.PARAM_ISCLEAN_SESSION));

				Integer keepAlive = Integer.parseInt(intent.getStringExtra(ApplicationSettings.PARAM_KEEPALIVE));

				WillParcel will = (WillParcel) intent.getParcelableExtra(WillParcel.class.getCanonicalName());

				int port = Integer.parseInt(intent.getStringExtra(ApplicationSettings.PARAM_PORT));
				Boolean shouldUpdate = Boolean.parseBoolean(intent
						.getStringExtra(ApplicationSettings.PARAM_SHOULD_UPDATE));

				InetSocketAddress address = null;

				address = InetSocketAddress.createUnresolved(serverHost, port);

				Intent brodcastIntent = new Intent();
				res = activateService(address, userName, userPass, clientID, isCleanSession, keepAlive, will);

				if (res) {
					brodcastIntent.setAction(ApplicationSettings.ACTION_CHANNEL_CREATING);
				}
				else {
					brodcastIntent.setAction(ApplicationSettings.ACTION_ERROR_OPENNING_CHANNEL);
				}

				brodcastIntent.putExtra(ApplicationSettings.PARAM_SERVER_HOST, serverHost);
				brodcastIntent.putExtra(ApplicationSettings.PARAM_PORT, Integer.toString(port));
				brodcastIntent.putExtra(ApplicationSettings.PARAM_USERNAME, userName);
				brodcastIntent.putExtra(ApplicationSettings.PARAM_PASSWORD, userPass);
				brodcastIntent.putExtra(ApplicationSettings.PARAM_CLIENTID, clientID);
				brodcastIntent.putExtra(ApplicationSettings.PARAM_ISCLEAN_SESSION, Boolean.toString(isCleanSession));
				brodcastIntent.putExtra(ApplicationSettings.PARAM_KEEPALIVE, keepAlive.toString());
				brodcastIntent.putExtra(ApplicationSettings.PARAM_SHOULD_UPDATE, shouldUpdate.toString());

				if (will != null) {
					WillParcel willParcel = new WillParcel(will);
					brodcastIntent.putExtra(WillParcel.class.getCanonicalName(), willParcel);
				}

				brodcastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
				sendBroadcast(brodcastIntent);

			}
			else if (action.equalsIgnoreCase(ApplicationSettings.ACTION_CHANGE_TMACTIVITY_VISIBLE)) {
				tmActivityVisible = Boolean.parseBoolean(intent.getStringExtra(ApplicationSettings.PARAM_IS_VISIBLE));

				/*
				 * Intent brodcastIntent = new Intent();
				 * brodcastIntent.putExtra(
				 * ApplicationSettings.ACTION_CHANGE_TMACTIVITY_VISIBLE, value)
				 * brodcastIntent
				 * .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
				 * sendBroadcast(brodcastIntent);
				 */
			}

			else if (action.equalsIgnoreCase(ApplicationSettings.ACTION_MESSAGE_RECEIVED)) {
				if (tmActivityVisible) {

					MessageType messageType = (MessageType) intent
							.getSerializableExtra(ApplicationSettings.PARAM_MESSAGETYPE);

					Intent brodcastIntent = new Intent();
					brodcastIntent.putExtra(ApplicationSettings.PARAM_MESSAGETYPE, messageType);
					brodcastIntent.setAction(ApplicationSettings.ACTION_MESSAGE_RECEIVED);

					sendBroadcast(brodcastIntent);
				}
				else {
					// show notification
					MessageType messageType = (MessageType) intent
							.getSerializableExtra(ApplicationSettings.PARAM_MESSAGETYPE);
					if (messageType == MessageType.PUBLISH)
						showNotification(this);
				}
			}

			else if (action.equalsIgnoreCase(ApplicationSettings.ACTION_SHOW_NOTIFICATION)) {
				showNotification(this);
			}

			else if (action.equalsIgnoreCase(ApplicationSettings.ACTION_SUBSCRIBE)) {
				String topicName = intent.getStringExtra(ApplicationSettings.PARAM_TOPIC_NAME);
				int qos = Integer.parseInt(intent.getStringExtra(ApplicationSettings.PARAM_QOS));

				subscribe(topicName, qos);
			}
			else if (action.equalsIgnoreCase(ApplicationSettings.ACTION_UNSUBSCRIBE)) {
				String topicName = intent.getStringExtra(ApplicationSettings.PARAM_TOPIC_NAME);
				int qos = Integer.parseInt(intent.getStringExtra(ApplicationSettings.PARAM_QOS));

				unsubscribe(topicName, qos);
			}
			else if (action.equalsIgnoreCase(ApplicationSettings.ACTION_PUBLISH)) {

				String content = intent.getStringExtra(ApplicationSettings.PARAM_CONTENT);
				String topicName = intent.getStringExtra(ApplicationSettings.PARAM_TOPIC);
				int qos = Integer.parseInt(intent.getStringExtra(ApplicationSettings.PARAM_QOS));
				String packedId = intent.getStringExtra(ApplicationSettings.PARAM_PACKET_ID);
				boolean isRetain = Boolean.parseBoolean(intent.getStringExtra(ApplicationSettings.PARAM_USERNAME));
				boolean isDublicate = Boolean.parseBoolean(intent.getStringExtra(ApplicationSettings.PARAM_USERNAME));

				publish(content, topicName, qos, packedId, isRetain, isDublicate);
			}
			else if (action.equalsIgnoreCase(ApplicationSettings.STATE_CHANGED)) {

				String connStateStr = intent.getStringExtra(ApplicationSettings.PARAM_STATE);			
				ConnectionState connState = ConnectionState.valueOf(connStateStr);
				stateChanged(connState);
			}

		}

		return super.onStartCommand(intent, flags, startId);
	}

	public static Boolean reactivate() {
		client.reinit();
		return client.createChannel();
	}

	private Boolean activateService(InetSocketAddress address, String username, String password, String clientID,
			boolean isClean, int keepalive, Will will) {

		if (client != null) {
			client.closeConnection();
			client = null;
		}

		client = new MqttClient(address, username, password, clientID, isClean, keepalive, will, NetworkService.this);
		client.setListener(instance);
		client.setDbListener(dbManager);

		return client.createChannel();
	}

	void deActivateService() {
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

	/*
	 * @SuppressWarnings({ "unchecked", "rawtypes" }) public static void
	 * executeAsyncTask(AsyncTask task, Object... strings) {
	 * task.execute(strings); }
	 */

	private void subscribe(String topicName, int qos) {
		if (client != null && client.checkConnected()) {
			QoS qosItem = QoS.valueOf(qos);
			Text topicNameTxt = new Text(topicName);
			Topic topic = new Topic(topicNameTxt, qosItem);

			Topic[] topics = new Topic[] { topic };
			client.subscribe(topics);
		}
	}

	private void unsubscribe(String topicName, int qos) {
		if (client != null && client.checkConnected()) {
			QoS qosItem = QoS.valueOf(qos);
			Text topicNameTxt = new Text(topicName);
			Topic topic = new Topic(topicNameTxt, qosItem);

			Topic[] topics = new Topic[] { topic };
			client.unsubscribe(topics);
		}
	}

	private void publish(String content, String topicName, int qos, String packedId, boolean isRetain,
			boolean isDublicate) {
		if (client != null && client.checkConnected()) {
			QoS qosItem = QoS.valueOf(qos);
			Text topicNameTxt = new Text(topicName);
			Topic topic = new Topic(topicNameTxt, qosItem);

			client.publish(topic, content.getBytes(), isRetain, isDublicate);
		}
	}

	@Override
	public void networkUp() {
		Intent addIntent = new Intent();
		addIntent.setAction(ApplicationSettings.NETWORK_UP);
		instance.getApplicationContext().sendBroadcast(addIntent);

	}

	@Override
	public void networkDown() {
		deActivateService();
		Intent addIntent = new Intent();
		addIntent.setAction(ApplicationSettings.NETWORK_DOWN);
		this.sendBroadcast(addIntent);
	}

	@Override
	public void networkChanged() {
		deActivateService();
		Intent addIntent = new Intent();
		addIntent.setAction(ApplicationSettings.NETWORK_CHANGED);
		this.sendBroadcast(addIntent);
	}

	public void writeError() {
		deActivateService();
		if (instance != null) {
			Intent addIntent = new Intent();
			addIntent.setAction(ApplicationSettings.NETWORK_DOWN);
			this.sendBroadcast(addIntent);
		}
	}

	@Override
	public void stateChanged(ConnectionState newState) {
		// Intent addIntent = new Intent();

		// addIntent.putExtra("status", newState.toString());
		// addIntent.setAction(ApplicationSettings.NETWORK_STATUS_CHANGE);
		// this.sendBroadcast(addIntent);

		
		Intent addIntent = new Intent();

		addIntent.putExtra("status", newState.toString());
		addIntent.setAction(ApplicationSettings.NETWORK_STATUS_CHANGE);
		this.sendBroadcast(addIntent);

		if (client != null) {
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

	private void showNotification(Context contextItem) {
		String message = contextItem.getResources().getString(R.string.notification_new_message);
		String title = contextItem.getResources().getString(R.string.notification_message);
		String notificationID_str = contextItem.getResources().getString(R.string.notification_id);
		int notificationID = Integer.parseInt(notificationID_str);

		Intent notificationIntent = new Intent(contextItem, LoadingActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(contextItem, 0, notificationIntent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		Notification.Builder builder = new Notification.Builder(contextItem);

		builder.setContentIntent(contentIntent).setSmallIcon(R.drawable.iotbroker_icon)
				.setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentTitle(title).setContentText(message);

		Notification notification = builder.build();

		NotificationManager notificationManager = (NotificationManager) contextItem
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(notificationID, notification);
	}
}