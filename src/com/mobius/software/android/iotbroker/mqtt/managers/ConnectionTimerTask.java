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

import com.mobius.software.android.iotbroker.mqtt.MqttClient;
import com.mobius.software.android.iotbroker.mqtt.services.NetworkService;

public class ConnectionTimerTask extends TimerTask 
{
	public static int REFRESH_PERIOD=500;
	private MqttClient client;
	
	public ConnectionTimerTask(MqttClient client)
	{
		this.client=client;
	}
	
	@Override
	public void run() 
	{
		if(NetworkService.hasInstance())
		{
			if(!client.checkCreated())
				client.executeTimer(new ConnectionTimerTask(client),REFRESH_PERIOD);
		}	
	}
}
