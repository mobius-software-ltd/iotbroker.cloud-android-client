package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.header.impl;

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

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MessageType;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MQTopic;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.CountableMessage;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;

public class Unsubscribe extends CountableMessage
{
	private MQTopic[] topics;

	public Unsubscribe(MQTopic[] topics)
	{
		this(null, topics);
	}

	public Unsubscribe(Integer packetID, MQTopic[] topics)
	{
		super(packetID);
		this.topics = topics;
	}

	@Override
	public int getLength()
	{
		int length = 2;
		for (MQTopic topic : topics)
			length += topic.getName().length() + 2;
		return length;
	}

	@Override
	public int getType()
	{
		return MessageType.UNSUBSCRIBE.getNum();
	}

	@Override
	public void processBy(Device device)
	{
		device.processMessage(this);
	}

	@Override
	public Protocols getProtocol() {
		return Protocols.MQTT_PROTOCOL;
	}

	public MQTopic[] getTopics()
	{
		return topics;
	}

	public void setTopics(MQTopic[] topics)
	{
		this.topics = topics;
	}
}
