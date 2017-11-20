package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.SimpleConstructor;
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

public class TLVNull extends TLVAmqp {

	public TLVNull() {
		super(new SimpleConstructor(AMQPType.NULL));
	}

	@Override
	public byte[] getBytes() {
		return constructor.getBytes();
	}

	@Override
	public int getLength() {
		return 1;
	}

	@Override
	public byte[] getValue() {
		return null;
	}

	@Override
	public String toString() {
		return "NULL";
	}

}
