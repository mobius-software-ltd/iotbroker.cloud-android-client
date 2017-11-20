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

public enum OutcomeCodes {

	OK((short) 0), AUTH((short) 1), SYS((short) 2), SYS_PERM((short) 3), SYS_TEMP((short) 4);

	private short type;

	private static Map<Short, OutcomeCodes> map = new HashMap<Short, OutcomeCodes>();

	static {
		for (OutcomeCodes legEnum : OutcomeCodes.values()) {
			map.put(legEnum.type, legEnum);
		}
	}

	public short getType() {
		return type;
	}

	private OutcomeCodes(final short leg) {
		type = leg;
	}

	public static OutcomeCodes valueOf(short code) {
		OutcomeCodes result = map.get(code);
		if (result == null)
			throw new InvalidCodeException("Unrecognized outcome-code: " + code);
		return result;
	}

}
