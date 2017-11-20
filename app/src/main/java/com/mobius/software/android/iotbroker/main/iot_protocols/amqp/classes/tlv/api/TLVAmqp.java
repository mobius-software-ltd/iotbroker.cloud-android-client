package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.SimpleConstructor;

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

public abstract class TLVAmqp {

	protected SimpleConstructor constructor;

	public TLVAmqp(SimpleConstructor constructor) {
		this.constructor = constructor;
	}

	public SimpleConstructor getConstructor() {
		return constructor;
	}

	public AMQPType getCode() {
		return constructor.getCode();
	}

	public void setConstructor(SimpleConstructor constructor) {
		this.constructor = constructor;
	}

	public void setCode(AMQPType code) {
		constructor.setCode(code);
	}

	public abstract byte[] getBytes();

	public abstract int getLength();

	public boolean isNull() {
		return constructor.getCode().equals(AMQPType.NULL);
	}

	public abstract byte[] getValue();
}
