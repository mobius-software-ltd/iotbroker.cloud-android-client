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

public enum TerminusDurability {

	NONE(0), CONFIGURATION(1), UNSETTLED_STATE(2);

	private long code;

	private static Map<Long, TerminusDurability> map = new HashMap<Long, TerminusDurability>();

	static {
		for (TerminusDurability legEnum : TerminusDurability.values()) {
			map.put(legEnum.code, legEnum);
		}
	}

	public Long getCode() {
		return code;
	}

	private TerminusDurability(final long leg) {
		code = leg;
	}

	public static TerminusDurability valueOf(long code) {
		TerminusDurability result = map.get(code);
		if (result == null)
			throw new InvalidCodeException("Unrecognized Terminus-durability code: " + code);
		return result;
	}
}
