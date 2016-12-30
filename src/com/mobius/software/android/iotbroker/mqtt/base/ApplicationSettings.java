package com.mobius.software.android.iotbroker.mqtt.base;

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

public class ApplicationSettings {

	public static final String PARAM_HOSTNAME = "PARAM_HOSTNAME";
	public static final String PARAM_SERVER_HOST = "PARAM_SERVER_HOST";
	public static final String PARAM_PORT = "PARAM_PORT";

	public static final String PARAM_USERNAME = "PARAM_USERNAME";
	public static final String PARAM_PASSWORD = "PARAM_PASSWORD";
	public static final String PARAM_CLIENTID = "PARAM_CLIENTID";
	public static final String PARAM_ISCLEAN_SESSION = "PARAM_ISCLEAN";
	public static final String PARAM_KEEPALIVE = "PARAM_KEEPALIVE";
	public static final String PARAM_WILL = "PARAM_WILL";
	public static final String PARAM_CONTENT = "PARAM_CONTENT";
	public static final String PARAM_RETAIN = "PARAM_RETAIN";
	public static final String PARAM_SHOULD_UPDATE = "PARAM_SHOULD_UPDATE";
	public static final String PARAM_IS_VISIBLE = "PARAM_IS_VISIBLE";
	public static final String PARAM_MESSAGETYPE = "com.mobius.software.android.iotbroker.MESSAGETYPE_PARAM";

	public static final String PARAM_QOS = "com.mobius.software.android.iotbroker.ACTION_SUBSCRIBE.PARAM_QOS";
	public static final String PARAM_TOPIC_NAME = "com.mobius.software.android.iotbroker.ACTION_SUBSCRIBE.PARAM_TOPIC_NAME";

	public static final String PARAM_IS_DUBLICATE = "com.mobius.software.android.iotbroker.ACTION_SUBSCRIBE.PARAM_IS_DUBLICATE";
	public static final String PARAM_IS_RETAIN = "com.mobius.software.android.iotbroker.ACTION_SUBSCRIBE.PARAM_IS_RETAIN";
	public static final String PARAM_PACKET_ID = "com.mobius.software.android.iotbroker.ACTION_SUBSCRIBE.PARAM_PACKET_ID";
	public static final String PARAM_TOPIC = "com.mobius.software.android.iotbroker.ACTION_SUBSCRIBE.PARAM_TOPIC";
	public static final String PARAM_STATE = "com.mobius.software.android.iotbroker.ACTION_SUBSCRIBE.PARAM_STATE";

	public static final String ACTION_CHANNEL_CREATING = "com.mobius.software.android.iotbroker.ACTION_CHANNEL_CREATING";
	public static final String ACTION_ERROR_OPENNING_CHANNEL = "com.mobius.software.android.iotbroker.ACTION_ERROR_OPENNING_CREATING";
	public static final String ACTION_DEACTIVATE_SERVICE = "com.mobius.software.android.iotbroker.ACTION_DEACTIVATE_SERVICE";
	public static final String ACTION_REACTIVATE_SERVICE = "com.mobius.software.android.iotbroker.ACTION_ACTION_REACTIVATE_SERVICE";
	public static final String ACTION_CHANGE_TMACTIVITY_VISIBLE = "com.mobius.software.android.iotbroker.ACTION_CHANGE_TMACTIVITY_VISIBLE";
	public static final String ACTION_MESSAGE_RECEIVED = "com.mobius.software.android.iotbroker.ACTION_MESSAGE_RECEIVED";
	public static final String ACTION_SHOW_NOTIFICATION = "com.mobius.software.android.iotbroker.ACTION_SHOW_NOTIFICATION";
	public static final String ACTION_SUBSCRIBE = "com.mobius.software.android.iotbroker.ACTION_SUBSCRIBE";
	public static final String ACTION_UNSUBSCRIBE = "com.mobius.software.android.iotbroker.ACTION_UNSUBSCRIBE";
	public static final String ACTION_PUBLISH = "com.mobius.software.android.iotbroker.ACTION_PUBLISH";

	public static final String NETWORK_UP = "com.mobius.software.android.iotbroker.NETWORK_UP";
	public static final String NETWORK_DOWN = "com.mobius.software.android.iotbroker.NETWORK_DOWN";
	public static final String NETWORK_CHANGED = "com.mobius.software.android.iotbroker.NETWORK_CHANGED";
	public static final String NETWORK_STATUS_CHANGE = "com.mobius.software.android.iotbroker.NETWORK_STATUS_CHANGE";
	public static final String STATE_CHANGED = "com.mobius.software.android.iotbroker.stateChanged";

}
