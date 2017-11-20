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

public enum HeaderCodes {

	PROTO(0x08), OPEN(0x10), BEGIN(0x11), ATTACH(0x12), FLOW(0x13), TRANSFER(0x14), DISPOSITION(0x15), DETACH(
			0x16), END(0x17), CLOSE(0x18), MECHANISMS(0x40), INIT(0x41), CHALLENGE(0x42), RESPONSE(
			0x43), OUTCOME(0x44), PING(0xff);

	private int type;

	private static Map<Integer, HeaderCodes> map = new HashMap<Integer, HeaderCodes>();

	static {
		for (HeaderCodes legEnum : HeaderCodes.values()) {
			map.put(legEnum.type, legEnum);
		}
	}

	public int getType() {
		return type;
	}

	private HeaderCodes(final int leg) {
		type = leg;
	}

	public static HeaderCodes valueOf(int code) {
		HeaderCodes result = map.get(code);
		if (result == null)
			throw new InvalidCodeException("Unrecognized header argument code: " + code);
		return result;
	}
}
