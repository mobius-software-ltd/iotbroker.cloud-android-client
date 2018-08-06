package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl;

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps.Radius;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps.SNType;

/**
 * Mobius Software LTD
 * Copyright 2015-2017, Mobius Software LTD
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

public class Encapsulated implements Message
{
	private Radius radius;
	private String wirelessNodeID;
	private Message message;

	public Encapsulated()
	{
		super();
	}

	public Encapsulated(Radius radius, String wirelessNodeID, Message message)
	{
		this.radius = radius;
		this.wirelessNodeID = wirelessNodeID;
		this.message = message;
	}

	public Encapsulated reInit(Radius radius, String wirelessNodeID, Message message)
	{
		this.radius = radius;
		this.wirelessNodeID = wirelessNodeID;
		this.message = message;
		return this;
	}

	@Override
	public int getLength()
	{
		int length = 3;
		if (wirelessNodeID != null)
			length += wirelessNodeID.length();
		return length;
	}

	@Override
	public int getType()
	{
		return SNType.ENCAPSULATED.getValue();
	}

	@Override
	public Protocols getProtocol() {
		return Protocols.MQTT_SN_PROTOCOL;
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}

	public Radius getRadius()
	{
		return radius;
	}

	public void setRadius(Radius radius)
	{
		this.radius = radius;
	}

	public String getWirelessNodeID()
	{
		return wirelessNodeID;
	}

	public void setWirelessNodeID(String wirelessNodeID)
	{
		this.wirelessNodeID = wirelessNodeID;
	}

	public Message getMessage()
	{
		return message;
	}

	public void setMessage(Message message)
	{
		this.message = message;
	}
}