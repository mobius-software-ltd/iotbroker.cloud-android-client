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

public enum ErrorCodes {

	INTERNAL_ERROR("amqp:internal-error"), NOT_FOUND("amqp:not-found"), UNAUTHORIZED_ACCESS(
			"amqp:unauthorized-access"), DECODE_ERROR("amqp:decode-error"), RESOURCE_LIMIT_EXCEEDED(
			"amqp:resource-limit-exceeded"), NOT_ALLOWED("amqp:not-allowed"), INVALID_FIELD(
			"amqp:invalid-field"), NOT_IMPLEMENTED("amqp:not-implemented"), RESOURCE_LOCKED(
			"amqp:resource-locked"), PRECONDITION_FAILED("amqp:precondition-failed"), RESOURCE_DELETED(
			"amqp:resource-deleted"), ILLEGAL_STATE("amqp:illegal-state"), FRAME_SIZE_TOO_SMALL(
			"amqp:frame-size-too-small"), CONNECTION_FORCED("amqp:connection-forced"), FRAMING_ERROR(
			"amqp:framing-error"), REDIRECTED("amqp:redirected"), WINDOW_VIOLATION(
			"amqp:window-violation"), ERRANT_LINK("amqp:errant-link"), HANDLE_IN_USE(
			"amqp:handle-in-use"), UNATTACHED_HANDLE("amqp:unattached-handle"), DETACH_FORCED(
			"amqp:detach-forced"), TRANSFER_LIMIT_EXCEEDED("amqp:transfer-limit-exceeded"), MESSAGE_SIZE_EXCEEDED(
			"amqp:message-size-exceeded"), REDIRECT("amqp:redirect"), STOLEN("amqp:stolen");

	private String condition;

	private static Map<String, ErrorCodes> map = new HashMap<String, ErrorCodes>();

	static {
		for (ErrorCodes legEnum : ErrorCodes.values()) {
			map.put(legEnum.condition, legEnum);
		}
	}

	public String getCondition() {
		return condition;
	}

	private ErrorCodes(final String leg) {
		condition = leg;
	}

	public static ErrorCodes getCondition(String condition) {
		ErrorCodes code = map.get(condition);
		if (code == null)
			throw new InvalidCodeException("Unrecognized error condition: " + condition);
		return code;
	}
}
