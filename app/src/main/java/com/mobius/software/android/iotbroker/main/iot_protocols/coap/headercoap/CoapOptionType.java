package com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap;

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

public enum CoapOptionType {
	IF_MATCH(1), URI_HOST(3), ETAG(4), IF_NONE_MATCH(5), OBSERVE(6), URI_PORT(7), LOCATION_PATH(
			8), URI_PATH(11), CONTENT_FORMAT(12), MAX_AGE(14), URI_QUERY(15), ACCEPT(
			17), LOCATION_QUERY(20), BLOCK_2(23), BLOCK_1(27), SIZE_2(28), PROXY_URI(35), PROXY_SCHEME(39), SIZE_1(60);

	private int type;

	private static Map<Integer, CoapOptionType> map = new HashMap<Integer, CoapOptionType>();

	static {
		for (CoapOptionType legEnum : CoapOptionType.values()) {
			map.put(legEnum.type, legEnum);
		}
	}

	private CoapOptionType(final int leg) {
		type = leg;
	}

	public static CoapOptionType valueOf(int type) {
		return map.get(type);
	}

	public int getType() {
		return type;
	}
}
