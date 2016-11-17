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

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

import com.mobius.software.android.iotbroker.mqtt.managers.ConnectResendTimerTask;
import com.mobius.software.android.iotbroker.mqtt.managers.MessageResendTimerTask;
import com.mobius.software.android.iotbroker.mqtt.managers.PingResendTimerTask;
import com.mobius.software.android.iotbroker.mqtt.net.TCPClient;
import com.mobius.software.android.iotbroker.mqtt.parser.header.api.CountableMessage;
import com.mobius.software.android.iotbroker.mqtt.parser.header.api.MQMessage;
import com.mobius.software.android.iotbroker.mqtt.parser.header.impl.Pingreq;

public class TimersMap {
	private static final int MAX_VALUE = 65535;
	private static final int FIRST_ID = 1;

	private TCPClient listener;
	private long period;

	private ConcurrentSkipListMap<Integer, MessageResendTimerTask> timersMap = new ConcurrentSkipListMap<Integer, MessageResendTimerTask>();
	private ConnectResendTimerTask connect;
	private PingResendTimerTask ping;
	private MqttClient client;
	
	public TimersMap(MqttClient client,TCPClient listener, Long period) {
		this.listener = listener;
		this.period = period;
		this.client=client;
	}

	public void store(MQMessage message) {
		MessageResendTimerTask timer = new MessageResendTimerTask(message,
				listener, this);
		Integer packetID = (timersMap.isEmpty() || timersMap.lastKey() == MAX_VALUE) ? FIRST_ID
				: timersMap.lastKey();
		do {
			if (timersMap.size() == MAX_VALUE)
				throw new IllegalStateException("outgoing identifier overflow");
			packetID++;
		} while (timersMap.putIfAbsent(packetID, timer) != null);
		CountableMessage countable = (CountableMessage) message;
		countable.setPacketID(packetID);
		client.executeTimer(timer, period);
	}

	public void refreshTimer(MQMessage message) {
		CountableMessage countable = (CountableMessage) message;
		MessageResendTimerTask timer = new MessageResendTimerTask(message,
				listener, this);
		MessageResendTimerTask oldTimer = timersMap.put(
				countable.getPacketID(), timer);
		if (oldTimer != null)
			oldTimer.stop();
		client.executeTimer(timer, period);
	}

	public MessageResendTimerTask remove(Integer packetID) {
		MessageResendTimerTask timer = timersMap.remove(packetID);
		if (timer != null)
			timer.stop();
		return timer;
	}

	public void stopAllTimers() {
		if (this.connect != null)
			connect.stop();

		if (this.ping != null)
			ping.stop();

		for (Iterator<Map.Entry<Integer, MessageResendTimerTask>> it = timersMap
				.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, MessageResendTimerTask> entry = it.next();
			entry.getValue().stop();
		}

		timersMap.clear();
	}

	public void storeConnectTimer(MQMessage message) {
		ConnectResendTimerTask timer = new ConnectResendTimerTask(message,
				listener, this);
		if (connect != null)
			connect.stop();

		connect = timer;
		client.executeTimer(connect, period);
	}

	public ConnectResendTimerTask stopConnectTimer() {
		if (connect != null)
			connect.stop();
		return connect;
	}

	public void startPingTimer(int keepalive) {
		PingResendTimerTask timer = new PingResendTimerTask(new Pingreq(),
				listener, this, keepalive);
		if (ping != null)
			ping.stop();
		ping = timer;

		client.executeTimer(ping, keepalive * 1000);
	}
}