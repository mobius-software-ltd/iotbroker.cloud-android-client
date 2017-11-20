package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.libraries;

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

public enum ConnectionProperties {
	PLATFORM("platform"), PRODUCT("product"), QPID_CLIENT_PID("qpid.client_pid"), QPID_CLIENT_PPID(
			"qpid.client_ppid"), QPID_CLIENT_PROCESS("qpid.client_process"), VERSION("version");

	private String name;

	private static Map<String, ConnectionProperties> map = new HashMap<String, ConnectionProperties>();

	static {
		for (ConnectionProperties legEnum : ConnectionProperties.values()) {
			map.put(legEnum.name, legEnum);
		}
	}

	public String getName() {
		return name;
	}

	private ConnectionProperties(final String leg) {
		name = leg;
	}

	public static ConnectionProperties checkName(String policy) {
		return map.get(policy);
	}
}
