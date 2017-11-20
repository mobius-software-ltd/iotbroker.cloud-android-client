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

import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.MqttClient;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.TimersMap;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps.SNType;
import com.mobius.software.android.iotbroker.main.net.InternetProtocol;
import com.mobius.software.android.iotbroker.main.net.TCPClient;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MessageType;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl.Publish;
import com.mobius.software.android.iotbroker.main.services.NetworkService;

public class MessageResendTimerTask extends TimerTask {

	private Message message;
	private InternetProtocol client;
	private TimersMap timersMap;
	private int period;

	public int getPeriod() {
		return period;
	}

	private AtomicBoolean status = null;

	public MessageResendTimerTask(Message message, InternetProtocol client, TimersMap timersMap, int period) {
		this.message = message;
		this.client = client;
		this.timersMap = timersMap;
		this.period = period;
		status = new AtomicBoolean(true);
	}

	@Override
	public void run() {
		if (NetworkService.getStatus() != ConnectionState.CONNECTION_LOST) {

			if (status.get()) {

				if (message instanceof Publish) {
					Publish publish = (Publish) message;
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
