package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.variable;

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

public class TLVVariable extends TLVAmqp {

	private byte[] value;

	private int width;

	public TLVVariable(AMQPType code, byte[] value) {
		super(new SimpleConstructor(code));
		this.value = value;
		width = value.length > 255 ? 4 : 1;
	}

	@Override
	public byte[] getBytes() {
		byte[] constructorBytes = constructor.getBytes();
		byte[] widthBytes = new byte[width];
		if (width == 1)
			widthBytes[0] = (byte) value.length;
		else if (width == 4)
			ByteBuffer.wrap(widthBytes).putInt(value.length);
		byte[] bytes = new byte[constructorBytes.length + width + value.length];
        
		System.arraycopy(constructorBytes,  0,  bytes, 0,                       constructorBytes.length);
		System.arraycopy(widthBytes,        0,  bytes, constructorBytes.length, width);
        
		if (value.length > 0)
			System.arraycopy(value, 0, bytes, constructorBytes.length + width, value.length);
		return bytes;
	}

	@Override
	public int getLength() {
		return value.length + constructor.getLength() + width;
	}

	@Override
	public byte[] getValue() {
		return value;
	}

}
