package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl;

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.CountableMessage;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps.SNType;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.exceptions.MalformedMessageException;

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

public class Register extends CountableMessage
{
	private int topicID;
	private String topicName;

	public Register()
	{
		super();
	}

	public Register(int topicID, int packetID, String topicName)
	{
		super(packetID);
		this.topicID = topicID;
		this.topicName = topicName;
	}

	public Register reInit(int topicID, int packetID, String topicName)
	{
		this.topicID = topicID;
		this.packetID = packetID;
		this.topicName = topicName;
		return this;
	}

	@Override
	public int getLength()
	{
		if (this.topicName == null)
			throw new MalformedMessageException(this.getClass().getSimpleName() + " must contain a valid topic name");
		int length = 6;
		length += topicName.length();
		if (topicName.length() > 249)
			length += 2;
		return length;
	}

	@Override
	public int getType()
	{
		return SNType.REGISTER.getValue();
	}

	@Override
	public Protocols getProtocol() {
		return Protocols.MQTT_SN_PROTOCOL;
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}

	public int getTopicID()
	{
		return topicID;
	}

	public void setTopicID(int topicID)
	{
		this.topicID = topicID;
	}

	public String getTopicName()
	{
		return topicName;
	}

	public void setTopicName(String topicName)
	{
		this.topicName = topicName;
	}
}
