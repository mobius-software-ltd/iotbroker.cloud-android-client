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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MessageType;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps.MQTopic;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.CountableMessage;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;

public class Publish extends CountableMessage
{
	private MQTopic topic;
	private byte[] content;
	private boolean retain;
	private boolean dup;

	public Publish() {
		super();
	}

	public Publish(MQTopic topic, byte[] content, boolean retain, boolean dup)
	{
		this(null, topic, content, retain, dup);
	}

	public Publish(Integer packetID, MQTopic topic, byte[] content, boolean retain, boolean dup)
	{
		super(packetID);
		this.topic = topic;
		this.content = content;
		this.retain = retain;
		this.dup = dup;
	}

	@Override
	public int getType()
	{
		return MessageType.PUBLISH.getNum();
	}

	@Override
	public void processBy(Device device)
	{
		device.processMessage(this);
	}

	@Override
	public int getLength()
	{
		int length = 0;
		length += packetID != null ? 2 : 0;
		length += topic.length() + 2;
		length += content.length;
		return length;
	}

	@Override
	public Protocols getProtocol() {
		return Protocols.MQTT_PROTOCOL;
	}

	public MQTopic getTopic()
	{
		return topic;
	}

	public void setTopic(MQTopic topic)
	{
		this.topic = topic;
	}

	public byte[] getContent()
	{
		return content;
	}

	public void setContent(byte[] content)
	{
		this.content = content;
	}

	public boolean isRetain()
	{
		return retain;
	}

	public void setRetain(boolean retain)
	{
		this.retain = retain;
	}

	public boolean isDup()
	{
		return dup;
	}

	public void setDup(boolean dup)
	{
		this.dup = dup;
	}
}
