package com.mobius.software.android.iotbroker.mqtt.managers;

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

import java.util.TimerTask;

import com.mobius.software.android.iotbroker.mqtt.TimersMap;
import com.mobius.software.android.iotbroker.mqtt.net.TCPClient;
import com.mobius.software.android.iotbroker.mqtt.parser.header.api.MQMessage;
import com.mobius.software.android.iotbroker.mqtt.services.NetworkService;

public class PingResendTimerTask extends TimerTask {

	private MQMessage message;
	private TCPClient client;
	private TimersMap timersMap;
	public Integer keepalive;
	private boolean status = true;

	public PingResendTimerTask(MQMessage message, TCPClient client,
			TimersMap timersMap, int keepalive) {
		this.message = message;
		this.client = client;
		this.timersMap = timersMap;
		this.keepalive = keepalive;

	}

	@Override
	public void run() {
		if (NetworkService.hasInstance()) {
			if (status && NetworkService.checkConnectStatus()) {
				client.send(message);
				timersMap.startPingTimer(keepalive);
			}
		}
	}

	public void stop() {
		status = false;
		this.cancel();
	}
}
