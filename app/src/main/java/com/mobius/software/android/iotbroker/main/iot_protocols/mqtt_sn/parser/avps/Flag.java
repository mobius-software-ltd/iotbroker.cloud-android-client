package com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps;

import java.util.HashMap;
import java.util.Map;

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

public enum Flag
{
	DUPLICATE(128), QOS_LEVEL_ONE(96), QOS_2(64), QOS_1(32), RETAIN(16), WILL(8), CLEAN_SESSION(4), RESERVED_TOPIC(3), SHORT_TOPIC(2), ID_TOPIC(1);

	private static final Map<Integer, Flag> intToTypeMap = new HashMap<Integer, Flag>();
	private static final Map<String, Flag> strToTypeMap = new HashMap<String, Flag>();

	static
	{
		for (Flag flags : Flag.values())
		{
			intToTypeMap.put((int) flags.value, flags);
			strToTypeMap.put(flags.name(), flags);
		}
	}

	private int value;

	Flag(final int value)
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}

	public static Flag valueOf(int type) throws MalformedMessageException
	{
		return intToTypeMap.get(type);
	}
}
