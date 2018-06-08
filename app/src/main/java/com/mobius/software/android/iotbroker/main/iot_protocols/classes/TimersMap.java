package com.mobius.software.android.iotbroker.main.iot_protocols.classes;

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

import android.util.Log;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.mobius.software.android.iotbroker.main.iot_protocols.IotProtocol;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.HeaderCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapCode;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapOptionType;
import com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap.CoapType;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps.SNType;
import com.mobius.software.android.iotbroker.main.managers.MessageResendTimerTask;
import com.mobius.software.android.iotbroker.main.net.InternetProtocol;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MessageType;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Pingreq;

public class TimersMap {
	private static final int MAX_VALUE = 65535;
	private static final int FIRST_ID = 1;

	private final int MESSAGE_RESEND_PERIOD = 3000;

	private InternetProtocol listener;

	private AtomicInteger currCount = new AtomicInteger(0);

	private ConcurrentHashMap<Integer, MessageResendTimerTask> timersMap = new ConcurrentHashMap<Integer, MessageResendTimerTask>();

	private MessageResendTimerTask connect;
	private MessageResendTimerTask ping;

	private IotProtocol client;

	public TimersMap(IotProtocol client, InternetProtocol listener) {
		this.listener = listener;
		this.client = client;
	}

	public void store(Message message) {
		MessageResendTimerTask timer = new MessageResendTimerTask(message, listener, this, MESSAGE_RESEND_PERIOD);

		Integer packetID = currCount.get();
		do {
			if (timersMap.size() == MAX_VALUE)
				throw new IllegalStateException("outgoing identifier overflow");
			packetID++;
			if (packetID == MAX_VALUE)
				packetID = FIRST_ID;
		} while (timersMap.putIfAbsent(packetID, timer) != null);

		CountableMessage countable = (CountableMessage) message;
		countable.setPacketID(packetID);

		client.executeTimer(timer, MESSAGE_RESEND_PERIOD);
	}

	public void refreshTimer(Message message) {

		int period = MESSAGE_RESEND_PERIOD;

		MessageResendTimerTask timer = new MessageResendTimerTask(message, listener, this, period);

		if ((message.getType() == MessageType.CONNECT.getNum() && message.getProtocol() == Protocols.MQTT_PROTOCOL) ||
				(message.getType() == SNType.CONNECT.getValue() && message.getProtocol() == Protocols.MQTT_SN_PROTOCOL)) {
			connect.stop();
			connect = timer;
		} else if ((message.getType() == MessageType.PINGREQ.getNum() && message.getProtocol() == Protocols.MQTT_PROTOCOL) ||
				(message.getType() == SNType.PINGREQ.getValue() && message.getProtocol() == Protocols.MQTT_SN_PROTOCOL) ||
				(message.getType() == CoapType.CONFIRMABLE.getType() && message.getProtocol() == Protocols.COAP_PROTOCOL) ||
				(message.getType() == HeaderCodes.PING.getType() && message.getProtocol() == Protocols.AMQP_PROTOCOL)) {
			//ping.stop();
			period = ping.getPeriod() * 1000;
			ping = new MessageResendTimerTask(message, listener, this, ping.getPeriod());
		} else {
			CountableMessage countable = (CountableMessage) message;
			MessageResendTimerTask oldTimer = timersMap.put(countable.getPacketID(), timer);
			if (oldTimer != null)
				oldTimer.stop();
		}

		client.executeTimer(timer, period);
	}

	public MessageResendTimerTask remove(long packetID) {
		MessageResendTimerTask timer = timersMap.remove((int) packetID);
		if (timer != null)
			timer.stop();
		return timer;
	}

	public void stopAllTimers() {
		if (this.connect != null)
			connect.stop();

		if (this.ping != null)
			ping.stop();


		for (Iterator<Map.Entry<Integer, MessageResendTimerTask>> it = timersMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<Integer, MessageResendTimerTask> entry = it.next();
			entry.getValue().stop();
		}

		timersMap.clear();
		currCount.set(0);
	}

	public void storeConnectTimer(Message message) {
		MessageResendTimerTask timer = new MessageResendTimerTask(message, listener, this, MESSAGE_RESEND_PERIOD);
		if (connect != null)
			connect.stop();

		connect = timer;
		client.executeTimer(connect, MESSAGE_RESEND_PERIOD);
	}

	public MessageResendTimerTask stopConnectTimer() {
		if (connect != null)
			connect.stop();
		return connect;
	}

	public void startPingTimer(int keepalive) {
		MessageResendTimerTask timer = new MessageResendTimerTask(client.getPingreqMessage(), listener, this, keepalive);
		if (ping != null)
			ping.stop();
		ping = timer;

		client.executeTimer(ping, keepalive * 1000);
	}
}