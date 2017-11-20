package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl;

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
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

public class GWInfo implements Message
{
	private int gwID;
	private String gwAddress;

	public GWInfo()
	{
		super();
	}

	public GWInfo(int gwID, String gwAddress)
	{
		this.gwID = gwID;
		this.gwAddress = gwAddress;
	}

	public GWInfo reInit(int gwID, String gwAddress)
	{
		this.gwID = gwID;
		this.gwAddress = gwAddress;
		return this;
	}

	@Override
	public int getLength()
	{
		int length = 3;
		if (gwAddress != null)
			length += gwAddress.length();
		return length;
	}

	@Override
	public int getType()
	{
		return SNType.GWINFO.getValue();
	}

	@Override
	public Protocols getProtocol() {
		return Protocols.MQTT_SN_PROTOCOL;
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}

	public int getGwID()
	{
		return gwID;
	}

	public void setGwID(int gwID)
	{
		this.gwID = gwID;
	}

	public String getGwAddress()
	{
		return gwAddress;
	}

	public void setGwAddress(String gwAddress)
	{
		this.gwAddress = gwAddress;
	}
}
