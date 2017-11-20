package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headersasl;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.HeaderCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.OutcomeCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.DescribedConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.exceptions.MalformedHeaderException;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPUnwrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPWrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
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

public class SASLOutcome extends AMQPHeader {

	private OutcomeCodes outcomeCode;
	private byte[] additionalData;

	public SASLOutcome() {
		super(HeaderCodes.OUTCOME);
	}

	@Override
	public TLVList getArguments() {

		TLVList list = new TLVList();

		if (outcomeCode == null)
			throw new MalformedHeaderException("SASL-Outcome header's code can't be null");
		list.addElement(0, AMQPWrapper.wrap(outcomeCode.getType()));

		if (additionalData != null)
			list.addElement(1, AMQPWrapper.wrap(additionalData));

		DescribedConstructor constructor = new DescribedConstructor(list.getCode(),
				new TLVFixed(AMQPType.SMALL_ULONG, new byte[] { 0x44 }));
		list.setConstructor(constructor);

		return list;
	}

	@Override
	public void fillArguments(TLVList list) {

		int size = list.getList().size();

		if (size == 0)
			throw new MalformedHeaderException("Received malformed SASL-Outcome header: code can't be null");

		if (size > 2)
			throw new MalformedHeaderException(
					"Received malformed SASL-Outcome header. Invalid number of arguments: " + size);

		if (size > 0) {
			TLVAmqp element = list.getList().get(0);
			if (element.isNull())
				throw new MalformedHeaderException("Received malformed SASL-Outcome header: code can't be null");
			outcomeCode = OutcomeCodes.valueOf(AMQPUnwrapper.unwrapUByte(element));
		}

		if (list.getList().size() > 1) {
			TLVAmqp element = list.getList().get(1);
			if (!element.isNull())
				additionalData = AMQPUnwrapper.unwrapBinary(element);
		}
	}

	public OutcomeCodes getOutcomeCode() {
		return outcomeCode;
	}

	public void setOutcomeCode(OutcomeCodes outcomeCode) {
		if (outcomeCode == null)
			throw new IllegalArgumentException("Outcome-code can't be assigned a null value");
		this.outcomeCode = outcomeCode;
	}

	public byte[] getAdditionalData() {
		return additionalData;
	}

	public void setAdditionalData(byte[] additionalData) {
		this.additionalData = additionalData;
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
		return HeaderCodes.OUTCOME.getType();
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}
}
