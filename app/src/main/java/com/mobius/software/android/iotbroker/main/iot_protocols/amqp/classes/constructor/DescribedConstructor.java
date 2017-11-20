package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;

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

public class DescribedConstructor extends SimpleConstructor {

	private TLVAmqp descriptor;

	public DescribedConstructor(AMQPType code, TLVAmqp descriptor) {
		super(code);
		this.descriptor = descriptor;
	}

	public TLVAmqp getDescriptor() {
		return descriptor;
	}

	@Override
	public byte[] getBytes() {
		byte[] descriptorBytes = descriptor.getBytes();
		byte[] bytes = new byte[descriptorBytes.length + 2];
		bytes[0] = 0;
		System.arraycopy(descriptorBytes, 0, bytes, 1, descriptorBytes.length);
		bytes[bytes.length - 1] = (byte) code.getType();
		return bytes;
	}

	@Override
	public int getLength() {
		return descriptor.getLength() + 2;
	}

	public Byte getDescriptorCode() {
		return descriptor.getBytes()[1];
	}

}
