package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.exceptions.InvalidCodeException;

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

public enum ReceiveCodes {

	FIRST((short) 0), SECOND((short) 1);

	private short type;

	private static Map<Short, ReceiveCodes> map = new HashMap<Short, ReceiveCodes>();

	static {
		for (ReceiveCodes legEnum : ReceiveCodes.values()) {
			map.put(legEnum.type, legEnum);
		}
	}

	public short getType() {
		return type;
	}

	private ReceiveCodes(final short leg) {
		type = leg;
	}

	public static ReceiveCodes valueOf(short code) {
		ReceiveCodes result = map.get(code);
		if (result == null)
			throw new InvalidCodeException("Unrecognized receive-code: " + code);
		return result;
	}

}
