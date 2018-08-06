package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.avps;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.QoS;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Topic;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps.TopicType;

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

public class MQTopic implements Topic
{
	private static final String SEPARATOR = ":";
	private Text name;
	private QoS qos;

	public MQTopic()
	{

	}

	public MQTopic(Text name, QoS qos)
	{
		this.name = name;
		this.qos = qos;
	}

	public String toString()
	{
		return name.toString() + SEPARATOR + qos;
	}

	public Text getName()
	{
		return name;
	}

	public void setName(Text name)
	{
		this.name = name;
	}

	@Override
	@JsonIgnore
	public TopicType getType() {
		return null;
	}

	@Override
	public QoS getQos()
	{
		return qos;
	}

	@Override
	public byte[] encode() {
		return this.name.toString().getBytes();
	}

	public void setQos(QoS qos)
	{
		this.qos = qos;
	}

	@Override
	public int length()
	{
		return name.length();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		MQTopic other = (MQTopic) obj;
		if (name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!name.equals(other.name))
			return false;
		return true;
	}

	public static MQTopic valueOf(Text topic, QoS qos)
	{
		return new MQTopic(topic, qos);
	}
}
