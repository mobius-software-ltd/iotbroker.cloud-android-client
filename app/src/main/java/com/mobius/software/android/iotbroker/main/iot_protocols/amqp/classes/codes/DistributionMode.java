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

public enum DistributionMode {

	MOVE("move"), COPY("copy");

	private String mode;

	private static Map<String, DistributionMode> map = new HashMap<String, DistributionMode>();

	static {
		for (DistributionMode legEnum : DistributionMode.values()) {
			map.put(legEnum.mode, legEnum);
		}
	}

	public String getMode() {
		return mode;
	}

	private DistributionMode(final String leg) {
		mode = leg;
	}

	public static DistributionMode getMode(String mode) {
		DistributionMode code = map.get(mode);
		if (code == null)
			throw new InvalidCodeException("Unrecignized Distribution-mode: " + mode);
		return code;
	}
}
