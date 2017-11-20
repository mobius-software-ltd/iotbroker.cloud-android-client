package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps;

import java.util.HashMap;
import java.util.Map;

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

public enum Control
{
	BROADCAST(0), RADIUS_1(1), RADIUS_2(2), RADIUS_3(3);

	private static final Map<Integer, Control> intToTypeMap = new HashMap<Integer, Control>();
	private static final Map<String, Control> strToTypeMap = new HashMap<String, Control>();

	static
	{
		for (Control control : Control.values())
		{
			intToTypeMap.put((int) control.value, control);
			strToTypeMap.put(control.name(), control);
		}
	}

	private int value;

	Control(final int value)
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}

	public static Control valueOf(int type)
	{
		return intToTypeMap.get(type);
	}
}
