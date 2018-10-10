package com.mobius.software.android.iotbroker.main.managers;

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

import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.mobius.software.android.iotbroker.main.iot_protocols.IotProtocol;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.MqttClient;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.TimersMap;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Connect;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps.SNType;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.SNConnect;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl.SNPublish;
import com.mobius.software.android.iotbroker.main.net.InternetProtocol;
import com.mobius.software.android.iotbroker.main.net.TCPClient;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MessageType;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Publish;
import com.mobius.software.android.iotbroker.main.services.NetworkService;

public class MessageResendTimerTask extends TimerTask {

	private static final Integer MAX_CONNECT_RESEND_TIMES = 5;

	private Message message;
	private IotProtocol client;
	private TimersMap timersMap;
	private int period;
	private int connectCount;

	public int getPeriod() {
		return period;
	}

	private AtomicBoolean status = null;

	public MessageResendTimerTask(Message message, IotProtocol client, TimersMap timersMap, int period) {
		this.message = message;
		this.client = client;
		this.timersMap = timersMap;
		this.period = period;
		status = new AtomicBoolean(true);
		connectCount = 0;
	}

	@Override
	public void run() {

		if (message instanceof Connect || message instanceof SNConnect) {
			if (this.connectCount >= MAX_CONNECT_RESEND_TIMES) {
				this.client.timeout();
				this.connectCount = 0;
				return;
			}
			this.connectCount += 1;
		}

		if (NetworkService.getStatus() != ConnectionState.CONNECTION_LOST) {

			if (status.get()) {

				if (message instanceof Publish) {
					Publish publish = (Publish) message;
					publish.setDup(true);
				}

				if (message instanceof SNPublish) {
					SNPublish publish = (SNPublish) message;
					publish.setDup(true);
				}

				client.send(message);
				timersMap.refreshTimer(message);
			}
		}
	}

	public void stop() {
		status = new AtomicBoolean(false);
		this.cancel();
	}

	public Message retrieveMessage() {
		return message;
	}
}
