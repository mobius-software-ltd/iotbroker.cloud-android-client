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

public enum Radius
{
	BROADCAST(0), RADIUS_1(1), RADIUS_2(2), RADIUS_3(3);

	private static final Map<Integer, Radius> intToTypeMap = new HashMap<Integer, Radius>();
	private static final Map<String, Radius> strToTypeMap = new HashMap<String, Radius>();

	static
	{
		for (Radius radius : Radius.values())
		{
			intToTypeMap.put((int) radius.value, radius);
			strToTypeMap.put(radius.name(), radius);
		}
	}

	private int value;

	Radius(final int value)
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}

	public static Radius valueOf(int type)
	{
		return intToTypeMap.get(type);
	}
}
