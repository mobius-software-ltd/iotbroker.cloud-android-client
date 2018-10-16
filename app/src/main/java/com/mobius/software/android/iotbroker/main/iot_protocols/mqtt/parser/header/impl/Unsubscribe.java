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
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.Text;

import java.util.List;

public class Unsubscribe extends CountableMessage
{
	private Text[] topics;

	public Unsubscribe() {
		super();
	}

	public Unsubscribe(Text[] topics)
	{
		this(null, topics);
	}

	public Unsubscribe(Integer packetID, Text[] topics)
	{
		super(packetID);
		this.topics = topics;
	}

	@Override
	public int getLength()
	{
		int length = 2;
		for (Text topic : topics)
			length += topic.length() + 2;
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

	public Text[] getTopics()
	{
		return topics;
	}

	public void setTopics(Text[] topics)
	{
		this.topics = topics;
	}
}
