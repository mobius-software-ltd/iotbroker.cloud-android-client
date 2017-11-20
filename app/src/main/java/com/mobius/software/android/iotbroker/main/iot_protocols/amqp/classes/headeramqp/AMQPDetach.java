package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.HeaderCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.DescribedConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.exceptions.MalformedHeaderException;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPUnwrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPWrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described.AMQPError;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVFixed;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;

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

public class AMQPDetach extends AMQPHeader {

	private Long handle;
	private Boolean closed;
	private AMQPError error;

	public AMQPDetach() {
		super(HeaderCodes.DETACH);
	}

	@Override
	public TLVList getArguments() {

		TLVList list = new TLVList();

		if (handle == null)
			throw new MalformedHeaderException("Detach header's handle can't be null");
		list.addElement(0, AMQPWrapper.wrap(handle));

		if (closed != null)
			list.addElement(1, AMQPWrapper.wrap(closed));

		if (error != null)
			list.addElement(2, error.getList());

		DescribedConstructor constructor = new DescribedConstructor(list.getCode(),
				new TLVFixed(AMQPType.SMALL_ULONG, new byte[] { (byte) code.getType() }));
		list.setConstructor(constructor);

		return list;
	}

	@Override
	public void fillArguments(TLVList list) {

		int size = list.getList().size();

		if (size == 0)
			throw new MalformedHeaderException("Received malformed Detach header: handle can't be null");

		if (size > 3)
			throw new MalformedHeaderException(
					"Received malformed Detach header. Invalid number of arguments: " + size);

		if (size > 0) {
			TLVAmqp element = list.getList().get(0);
			if (element.isNull())
				throw new MalformedHeaderException("Received malformed Detach header: handle can't be null");
			handle = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 1) {
			TLVAmqp element = list.getList().get(1);
			if (!element.isNull())
				closed = AMQPUnwrapper.unwrapBool(element);
		}
		if (size > 2) {
			TLVAmqp element = list.getList().get(2);
			if (!element.isNull()) {
				AMQPType code = element.getCode();
				if (code != AMQPType.LIST_0 && code != AMQPType.LIST_8 && code != AMQPType.LIST_32)
					throw new MalformedHeaderException("Expected type 'ERROR' - received: " + element.getCode());
				error = new AMQPError();
				error.fill((TLVList) element);
			}
		}
	}

	public Long getHandle() {
		return handle;
	}

	public void setHandle(Long handle) {
		if (handle == null)
			throw new IllegalArgumentException("Handle can't be assigned a null value");
		this.handle = handle;
	}

	public Boolean getClosed() {
		return closed;
	}

	public void setClosed(Boolean closed) {
		this.closed = closed;
	}

	public AMQPError getError() {
		return error;
	}

	public void setError(AMQPError error) {
		this.error = error;
	}

	@Override
	public int getLength() {
		int length = 8;
		TLVAmqp arguments = this.getArguments();
		length += arguments.getLength();
		return length;
	}

	@Override
	public int getType() {
		return HeaderCodes.DETACH.getType();
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}
}
