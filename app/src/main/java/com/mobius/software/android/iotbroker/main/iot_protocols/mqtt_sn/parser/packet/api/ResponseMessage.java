package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.packet.api;

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps.ReturnCode;

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

public abstract class ResponseMessage implements Message
{
	private ReturnCode code;

	public ResponseMessage()
	{
		super();
	}

	public ResponseMessage(ReturnCode code)
	{
		this.code = code;
	}

	public ResponseMessage reInit(ReturnCode code)
	{
		this.code = code;
		return this;
	}

	@Override
	public int getLength()
	{
		return 3;
	}

	public ReturnCode getCode()
	{
		return code;
	}

	public void setCode(ReturnCode code)
	{
		this.code = code;
	}
}
