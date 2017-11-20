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

public class AMQPDecimal {

	private final byte[] value;

	public AMQPDecimal(byte[] value) {
		this.value = value;
	}

	public AMQPDecimal(byte b) {
		this.value = ByteBuffer.allocate(1).put(b).array();
	}

	public AMQPDecimal(short s) {
		this.value = ByteBuffer.allocate(2).putShort(s).array();
	}

	public AMQPDecimal(int i) {
		this.value = ByteBuffer.allocate(4).putInt(i).array();
	}

	public AMQPDecimal(long l) {
		this.value = ByteBuffer.allocate(8).putLong(l).array();
	}

	public AMQPDecimal(float f) {
		this.value = ByteBuffer.allocate(4).putFloat(f).array();
	}

	public AMQPDecimal(double d) {
		this.value = ByteBuffer.allocate(8).putDouble(d).array();
	}

	public double getDouble() {
		return ByteBuffer.wrap(value).getDouble();
	}

	public long getLong() {
		return ByteBuffer.wrap(value).getLong();
	}

	public int getInt() {
		return ByteBuffer.wrap(value).getInt();
	}

	public float getFloat() {
		return ByteBuffer.wrap(value).getFloat();
	}

	public short getShort() {
		return ByteBuffer.wrap(value).getShort();
	}

	public byte getByte() {
		return ByteBuffer.wrap(value).get();
	}

	public byte[] getValue() {
		return value;
	}

}
