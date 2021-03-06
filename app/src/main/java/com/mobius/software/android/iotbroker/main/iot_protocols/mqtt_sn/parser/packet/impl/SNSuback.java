package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl;

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.CountableMessage;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.QoS;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps.ReturnCode;
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

public class SNSuback extends CountableMessage
{
	private int topicID;
	private ReturnCode code;
	private QoS allowedQos;

	public SNSuback()
	{
		super();
	}

	public SNSuback(int topicID, int packetID, ReturnCode code, QoS allowedQos)
	{
		super(packetID);
		this.topicID = topicID;
		this.code = code;
		this.allowedQos = allowedQos;
	}

	public SNSuback reInit(int topicID, int packetID, ReturnCode code, QoS allowedQos)
	{
		setPacketID(packetID);
		this.topicID = topicID;
		this.code = code;
		this.allowedQos = allowedQos;
		return this;
	}

	@Override
	public int getLength()
	{
		return 8;
	}

	@Override
	public int getType()
	{
		return SNType.SUBACK.getValue();
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

	public ReturnCode getCode()
	{
		return code;
	}

	public void setCode(ReturnCode code)
	{
		this.code = code;
	}

	public QoS getAllowedQos()
	{
		return allowedQos;
	}

	public void setAllowedQos(QoS allowedQos)
	{
		this.allowedQos = allowedQos;
	}
}
