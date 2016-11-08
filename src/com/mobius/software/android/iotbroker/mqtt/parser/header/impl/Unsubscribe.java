package com.mobius.software.android.iotbroker.mqtt.parser.header.impl;

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

import com.mobius.software.android.iotbroker.mqtt.parser.Topic;
import com.mobius.software.android.iotbroker.mqtt.parser.header.api.CountableMessage;
import com.mobius.software.android.iotbroker.mqtt.parser.header.api.MQDevice;

public class Unsubscribe implements CountableMessage
{
	private Integer packetID;
	private Topic[] topics;

	public Unsubscribe(Topic[] topics)
	{
		this(null, topics);
	}

	public Unsubscribe(Integer packetID, Topic[] topics)
	{
		this.packetID = packetID;
		this.topics = topics;
	}

	@Override
	public int getLength()
	{
		int length = 2;
		for (Topic topic : topics)
			length += topic.getName().length() + 2;
		return length;
	}

	@Override
	public MessageType getType()
	{
		return MessageType.UNSUBSCRIBE;
	}

	@Override
	public void processBy(MQDevice device)
	{
		device.processUnsubscribe(packetID, topics);
	}

	public Integer getPacketID()
	{
		return packetID;
	}

	public void setPacketID(Integer packetID)
	{
		this.packetID = packetID;
	}

	public Topic[] getTopics()
	{
		return topics;
	}

	public void setTopics(Topic[] topics)
	{
		this.topics = topics;
	}
}
