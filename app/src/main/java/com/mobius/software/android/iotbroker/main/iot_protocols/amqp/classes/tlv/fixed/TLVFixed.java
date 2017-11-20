package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.SimpleConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;

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

public class TLVFixed extends TLVAmqp {

	private byte[] value;

	public TLVFixed(AMQPType code, byte[] value) {
		super(new SimpleConstructor(code));
		this.value = value;
	}

	@Override
	public byte[] getBytes() {
		byte[] constructorBytes = constructor.getBytes();
		byte[] bytes = new byte[constructorBytes.length + value.length];
		System.arraycopy(constructorBytes, 0, bytes, 0, constructorBytes.length);
		if (value.length > 0)
			System.arraycopy(value, 0, bytes, constructorBytes.length, value.length);
		return bytes;
	}

	@Override
	public int getLength() {
		return value.length + constructor.getLength();
	}

	@Override
	public byte[] getValue() {
		return value;
	}

	// TODO
	@Override
	public String toString() {
		String s = null;

		switch (constructor.getCode()) {

		case BOOLEAN_TRUE:
			s = "1";
			break;

		case BOOLEAN_FALSE:
		case UINT_0:
		case ULONG_0:
			s = "0";
			break;

		case BOOLEAN:
		case BYTE:
		case UBYTE:
		case SMALL_INT:
		case SMALL_LONG:
		case SMALL_UINT:
		case SMALL_ULONG:
			s = Byte.toString(value[0]);
			break;

		case SHORT:
		case USHORT:
			s = Short.toString(ByteBuffer.wrap(value).getShort());
			break;

		case CHAR:
		case DECIMAL_32:
		case FLOAT:
		case INT:
		case UINT:
			s = Integer.toString(ByteBuffer.wrap(value).getInt());
			break;

		case DECIMAL_64:
		case DOUBLE:
		case LONG:
		case ULONG:
		case TIMESTAMP:
			s = Long.toString(ByteBuffer.wrap(value).getLong());
			break;

		case DECIMAL_128:
			s = "decimal-128";
			break;

		case UUID:
			s = new String(value);
			break;

		default:
			break;
		}

		return s;
	}

}
