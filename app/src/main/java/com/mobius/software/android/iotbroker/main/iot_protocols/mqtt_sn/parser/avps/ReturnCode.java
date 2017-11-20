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

public enum ReturnCode
{
	ACCEPTED(0), CONGESTION(1), INVALID_TOPIC_ID(2), NOT_SUPPORTED(3);

	private int value;

	private static Map<Integer, ReturnCode> map = new HashMap<Integer, ReturnCode>();

	static
	{
		for (ReturnCode code : ReturnCode.values())
		{
			map.put(code.value, code);
		}
	}

	public int getValue()
	{
		return value;
	}

	private ReturnCode(final int value)
	{
		this.value = value;
	}

	public static ReturnCode valueOf(int type) throws MalformedMessageException
	{
		ReturnCode result = map.get(type);
		if (result == null)
			throw new MalformedMessageException(String.format("Return code undefined: %d", type));
		return result;
	}
}
