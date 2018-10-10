package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps;

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.QoS;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Topic;

import java.nio.ByteBuffer;

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

public class IdentifierTopic implements Topic
{
	private int value;
	private QoS qos;

	public IdentifierTopic()
	{
		super();
	}

	public static short valueByBytes(byte[] bytes) {
		return ByteBuffer.wrap(bytes).getShort();
	}

	public IdentifierTopic(int value, QoS qos)
	{
		this.value = value;
		this.qos = qos;
	}

	public IdentifierTopic reInit(int value, QoS qos)
	{
		this.value = value;
		this.qos = qos;
		return this;
	}

	@Override
	public TopicType getType()
	{
		return TopicType.ID;
	}

	@Override
	public byte[] encode()
	{
		return ByteBuffer.allocate(2).putShort((short) value).array();
	}

	@Override
	public int length()
	{
		return 2;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((qos == null) ? 0 : qos.hashCode());
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdentifierTopic other = (IdentifierTopic) obj;
		if (qos != other.qos)
			return false;
		if (value != other.value)
			return false;
		return true;
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}

	public QoS getQos()
	{
		return qos;
	}

	public void setQos(QoS qos)
	{
		this.qos = qos;
	}
}
