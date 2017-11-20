package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.HeaderCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.RoleCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.DescribedConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.exceptions.MalformedHeaderException;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPFactory;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPUnwrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPWrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described.AMQPState;
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

public class AMQPDisposition extends AMQPHeader {

	private RoleCodes role;
	private Long first;
	private Long last;
	private Boolean settled;
	private AMQPState state;
	private Boolean batchable;

	public AMQPDisposition() {
		super(HeaderCodes.DISPOSITION);
	}

	@Override
	public TLVList getArguments() {

		TLVList list = new TLVList();

		if (role == null)
			throw new MalformedHeaderException("Disposition header's role can't be null");
		list.addElement(0, AMQPWrapper.wrap(role.getType()));

		if (first == null)
			throw new MalformedHeaderException("Transfer header's first can't be null");
		list.addElement(1, AMQPWrapper.wrap(first));

		if (last != null)
			list.addElement(2, AMQPWrapper.wrap(last));
		if (settled != null)
			list.addElement(3, AMQPWrapper.wrap(settled));
		if (state != null)
			list.addElement(4, state.getList());
		if (batchable != null)
			list.addElement(5, AMQPWrapper.wrap(batchable));

		DescribedConstructor constructor = new DescribedConstructor(list.getCode(),
				new TLVFixed(AMQPType.SMALL_ULONG, new byte[] { (byte) code.getType() }));
		list.setConstructor(constructor);

		return list;
	}

	@Override
	public void fillArguments(TLVList list) {

		int size = list.getList().size();

		if (size < 2)
			throw new MalformedHeaderException("Received malformed Disposition header: role and first can't be null");

		if (size > 6)
			throw new MalformedHeaderException(
					"Received malformed Disposition header. Invalid number of arguments: " + size);

		if (size > 0) {
			TLVAmqp element = list.getList().get(0);
			if (element.isNull())
				throw new MalformedHeaderException("Received malformed Disposition header: role can't be null");
			role = RoleCodes.valueOf(AMQPUnwrapper.unwrapBool(element));
		}
		if (size > 1) {
			TLVAmqp element = list.getList().get(1);
			if (element.isNull())
				throw new MalformedHeaderException("Received malformed Disposition header: first can't be null");
			first = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 2) {
			TLVAmqp element = list.getList().get(2);
			if (!element.isNull())
				last = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 3) {
			TLVAmqp element = list.getList().get(3);
			if (!element.isNull())
				settled = AMQPUnwrapper.unwrapBool(element);
		}
		if (size > 4) {
			TLVAmqp element = list.getList().get(4);
			if (!element.isNull()) {
				AMQPType code = element.getCode();
				if (code != AMQPType.LIST_0 && code != AMQPType.LIST_8 && code != AMQPType.LIST_32)
					throw new MalformedHeaderException("Expected type 'STATE' - received: " + element.getCode());
				state = AMQPFactory.getState((TLVList) element);
				state.fill((TLVList) element);
			}
		}
		if (size > 5) {
			TLVAmqp element = list.getList().get(5);
			if (!element.isNull())
				batchable = AMQPUnwrapper.unwrapBool(element);
		}
	}

	public RoleCodes getRole() {
		return role;
	}

	public void setRole(RoleCodes role) {
		if (role == null)
			throw new IllegalArgumentException("Role can't be assigned a null value");
		this.role = role;
	}

	public Long getFirst() {
		return first;
	}

	public void setFirst(Long first) {
		if (first == null)
			throw new IllegalArgumentException("First can't be assigned a null value");
		this.first = first;
	}

	public Long getLast() {
		return last;
	}

	public void setLast(Long last) {
		this.last = last;
	}

	public Boolean getSettled() {
		return settled;
	}

	public void setSettled(Boolean settled) {
		this.settled = settled;
	}

	public AMQPState getState() {
		return state;
	}

	public void setState(AMQPState state) {
		this.state = state;
	}

	public Boolean getBatchable() {
		return batchable;
	}

	public void setBatchable(Boolean batchable) {
		this.batchable = batchable;
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
		return HeaderCodes.DISPOSITION.getType();
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}
}
