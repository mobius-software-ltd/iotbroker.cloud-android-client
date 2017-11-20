package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers;

import java.nio.ByteBuffer;

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

public class AMQPMessageFormat {

	private final int messageFormat;
	private final int version;

	public AMQPMessageFormat(long value) {
		byte[] arr = ByteBuffer.allocate(4).putInt((int) value).array();
		byte[] mf = new byte[4];
		System.arraycopy(arr, 0, mf, 1, 3);
		messageFormat = ByteBuffer.wrap(mf).getInt();
		version = arr[3] & 0xff;
	}

	public AMQPMessageFormat(int messageFormat, int version) {
		this.messageFormat = messageFormat;
		this.version = version;
	}

	public int getMessageFormat() {
		return messageFormat;
	}

	public int getVersion() {
		return version;
	}

	public Long encode() {
		byte[] arr = new byte[4];
		byte[] mf = ByteBuffer.allocate(4).putInt(messageFormat).array();
		System.arraycopy(mf, 1, arr, 0, 3);
		arr[3] = (byte) version;
		return (long) ByteBuffer.wrap(arr).getInt();
	}

}
