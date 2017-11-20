package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.impl;

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

public class SNConnect implements Message
{
	public static final int MQTT_SN_PROTOCOL_ID = 1;

	private boolean willPresent;
	private boolean cleanSession;
	private int protocolID = MQTT_SN_PROTOCOL_ID;
	private int duration;
	private String clientID;

	public SNConnect()
	{
		super();
	}

	public SNConnect(boolean cleanSession, int duration, String clientID, boolean willPresent)
	{
		this.cleanSession = cleanSession;
		this.duration = duration;
		this.clientID = clientID;
		this.willPresent = willPresent;
	}

	public SNConnect reInit(boolean cleanSession, int duration, String clientID, boolean willPresent)
	{
		this.cleanSession = cleanSession;
		this.duration = duration;
		this.clientID = clientID;
		this.willPresent = willPresent;
		return this;
	}

	@Override
	public int getLength()
	{
		if (clientID == null || clientID.isEmpty())
			throw new MalformedMessageException("connect must contain a valid clientID");
		return 6 + clientID.length();
	}

	@Override
	public int getType()
	{
		return SNType.CONNECT.getValue();
	}

	@Override
	public Protocols getProtocol() {
		return Protocols.MQTT_SN_PROTOCOL;
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}

	public boolean isWillPresent()
	{
		return willPresent;
	}

	public void setWillPresent(boolean willPresent)
	{
		this.willPresent = willPresent;
	}

	public boolean isCleanSession()
	{
		return cleanSession;
	}

	public void setCleanSession(boolean cleanSession)
	{
		this.cleanSession = cleanSession;
	}

	public int getProtocolID()
	{
		return protocolID;
	}

	public void setProtocolID(int protocolID)
	{
		this.protocolID = protocolID;
	}

	public int getDuration()
	{
		return duration;
	}

	public void setDuration(int duration)
	{
		this.duration = duration;
	}

	public String getClientID()
	{
		return clientID;
	}

	public void setClientID(String clientID)
	{
		this.clientID = clientID;
	}
}
