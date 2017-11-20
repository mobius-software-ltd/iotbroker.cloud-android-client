package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headersasl;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.HeaderCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.DescribedConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.exceptions.MalformedHeaderException;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPUnwrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPWrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVFixed;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.AMQPSymbol;
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

public class SASLInit extends AMQPHeader {

	private AMQPSymbol mechanism;
	private byte[] initialResponse;
	private String hostName;

	public SASLInit() {
		super(HeaderCodes.INIT);
	}

	@Override
	public TLVList getArguments() {

		TLVList list = new TLVList();

		if (mechanism == null)
			throw new MalformedHeaderException("SASL-Init header's mechanism can't be null");
		list.addElement(0, AMQPWrapper.wrap(mechanism));

		if (initialResponse != null)
			list.addElement(1, AMQPWrapper.wrap(initialResponse));
		if (hostName != null)
			list.addElement(2, AMQPWrapper.wrap(hostName));

		DescribedConstructor constructor = new DescribedConstructor(list.getCode(),
				new TLVFixed(AMQPType.SMALL_ULONG, new byte[] { 0x41 }));
		list.setConstructor(constructor);

		return list;
	}

	@Override
	public void fillArguments(TLVList list) {

		int size = list.getList().size();

		if (size == 0)
			throw new MalformedHeaderException("Received malformed SASL-Init header: mechanism can't be null");

		if (size > 3)
			throw new MalformedHeaderException(
					"Received malformed SASL-Init header. Invalid number of arguments: " + size);

		if (size > 0) {
			TLVAmqp element = list.getList().get(0);
			if (element.isNull())
				throw new MalformedHeaderException("Received malformed SASL-Init header: mechanism can't be null");
			mechanism = AMQPUnwrapper.unwrapSymbol(element);
		}

		if (size > 1) {
			TLVAmqp element = list.getList().get(1);
			if (!element.isNull())
				initialResponse = AMQPUnwrapper.unwrapBinary(element);
		}

		if (size > 2) {
			TLVAmqp element = list.getList().get(2);
			if (!element.isNull())
				hostName = AMQPUnwrapper.unwrapString(element);
		}
	}

	public String getMechanism() {
		return mechanism.getValue();
	}

	public void setMechanism(String mechanism) {
		if (mechanism == null)
			throw new IllegalArgumentException("Mechanism can't be assigned a null value");
		this.mechanism = new AMQPSymbol(mechanism);
	}

	public byte[] getInitialResponse() {
		return initialResponse;
	}

	public void setInitialResponse(byte[] initialResponse) {
		this.initialResponse = initialResponse;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
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
		return HeaderCodes.INIT.getType();
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}
}
