package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl;

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.CountableMessage;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Topic;
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

public class SNUnsubscribe extends CountableMessage
{
	private Topic topic;

	public SNUnsubscribe()
	{
		super();
	}

	public SNUnsubscribe(int packetID, Topic topic)
	{
		super(packetID);
		this.topic = topic;
	}

	public SNUnsubscribe reInit(int packetID, Topic topic)
	{
		this.packetID = packetID;
		this.topic = topic;
		return this;
	}

	@Override
	public int getLength()
	{
		int length = 5;
		length += topic.length();
		if (topic.length() > 250)
			length += 2;
		return length;
	}

	@Override
	public int getType()
	{
		return SNType.UNSUBSCRIBE.getValue();
	}

	@Override
	public Protocols getProtocol() {
		return Protocols.MQTT_SN_PROTOCOL;
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}

	public Topic getTopic()
	{
		return topic;
	}

	public void setTopic(Topic topic)
	{
		this.topic = topic;
	}
}
