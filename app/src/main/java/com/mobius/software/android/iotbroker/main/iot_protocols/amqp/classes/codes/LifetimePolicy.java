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

public enum LifetimePolicy {

	DELETE_ON_CLOSE(0x2b), DELETE_ON_NO_LINKS(0x2c), DELETE_ON_NO_MESSAGES(0x2d), DELETE_ON_NO_LINKS_OR_MESSAGES(
			0x2e);

	private int policy;

	private static Map<Integer, LifetimePolicy> map = new HashMap<Integer, LifetimePolicy>();

	static {
		for (LifetimePolicy legEnum : LifetimePolicy.values()) {
			map.put(legEnum.policy, legEnum);
		}
	}

	public int getPolicy() {
		return policy;
	}

	private LifetimePolicy(final int leg) {
		policy = leg;
	}

	public static LifetimePolicy valueOf(int code) {
		LifetimePolicy result = map.get(code);
		if (result == null)
			throw new InvalidCodeException("Unrecognized lifetime-policy code: " + code);
		return result;
	}

}
