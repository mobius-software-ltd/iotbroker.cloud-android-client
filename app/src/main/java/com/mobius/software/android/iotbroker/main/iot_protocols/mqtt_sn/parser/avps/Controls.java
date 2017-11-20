package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps;

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

public class Controls
{
	private Radius radius;

	private Controls(Radius radius)
	{
		this.radius = radius;
	}

	public static Controls decode(byte ctrlByte)
	{
		if (ctrlByte > 3 || ctrlByte < 0)
			throw new MalformedMessageException("Invalid Encapsulated message control encoding:" + ctrlByte);
		
		return new Controls(Radius.valueOf(ctrlByte));
	}

	public static byte encode(Radius radius)
	{
		byte ctrlByte = 0;
		ctrlByte |= radius.getValue();
		return ctrlByte;
	}

	public Radius getRadius()
	{
		return radius;
	}

	public void setRadius(Radius radius)
	{
		this.radius = radius;
	}
}
