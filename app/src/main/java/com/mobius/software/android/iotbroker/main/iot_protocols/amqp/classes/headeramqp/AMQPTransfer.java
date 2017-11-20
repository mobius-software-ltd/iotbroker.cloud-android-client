package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.HeaderCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.ReceiveCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.SectionCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.DescribedConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.exceptions.MalformedHeaderException;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPFactory;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPUnwrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPWrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.AMQPSection;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described.AMQPState;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVFixed;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.AMQPMessageFormat;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

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

public class AMQPTransfer extends AMQPHeader {

	private Long handle;
	private Long deliveryId;
	private byte[] deliveryTag;
	private AMQPMessageFormat messageFormat;
	private Boolean settled;
	private Boolean more;
	private ReceiveCodes rcvSettleMode;
	private AMQPState state;
	private Boolean resume;
	private Boolean aborted;
	private Boolean batchable;
	private Map<SectionCodes, AMQPSection> sections;

	public AMQPTransfer() {
		super(HeaderCodes.TRANSFER);
	}

	@Override
	public TLVList getArguments() {

		TLVList list = new TLVList();

		if (handle == null)
			throw new MalformedHeaderException("Transfer header's handle can't be null");
		list.addElement(0, AMQPWrapper.wrap(handle));

		if (deliveryId != null)
			list.addElement(1, AMQPWrapper.wrap(deliveryId));
		if (deliveryTag != null)
			list.addElement(2, AMQPWrapper.wrap(deliveryTag));
		if (messageFormat != null)
			list.addElement(3, AMQPWrapper.wrap(messageFormat.encode()));
		if (settled != null)
			list.addElement(4, AMQPWrapper.wrap(settled));
		if (more != null)
			list.addElement(5, AMQPWrapper.wrap(more));
		if (rcvSettleMode != null)
			list.addElement(6, AMQPWrapper.wrap(rcvSettleMode.getType()));
		if (state != null)
			list.addElement(7, state.getList());
		if (resume != null)
			list.addElement(8, AMQPWrapper.wrap(resume));
		if (aborted != null)
			list.addElement(9, AMQPWrapper.wrap(aborted));
		if (batchable != null)
			list.addElement(10, AMQPWrapper.wrap(batchable));

		DescribedConstructor constructor = new DescribedConstructor(list.getCode(), new TLVFixed(
				AMQPType.SMALL_ULONG, new byte[] { (byte) code.getType() }));
		list.setConstructor(constructor);

		return list;
	}

	@Override
	public void fillArguments(TLVList list) {

		int size = list.getList().size();

		if (size == 0)
			throw new MalformedHeaderException(
					"Received malformed Transfer header: handle can't be null");

		if (size > 11)
			throw new MalformedHeaderException(
					"Received malformed Transfer header. Invalid number of arguments: " + size);

		if (size > 0) {
			TLVAmqp element = list.getList().get(0);
			if (element.isNull())
				throw new MalformedHeaderException(
						"Received malformed Transfer header: handle can't be null");
			handle = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 1) {
			TLVAmqp element = list.getList().get(1);
			if (!element.isNull())
				deliveryId = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 2) {
			TLVAmqp element = list.getList().get(2);
			if (!element.isNull())
				deliveryTag = AMQPUnwrapper.unwrapBinary(element);
		}
		if (size > 3) {
			TLVAmqp element = list.getList().get(3);
			if (!element.isNull())
				messageFormat = new AMQPMessageFormat(AMQPUnwrapper.unwrapUInt(element));
		}
		if (size > 4) {
			TLVAmqp element = list.getList().get(4);
			if (!element.isNull())
				settled = AMQPUnwrapper.unwrapBool(element);
		}
		if (size > 5) {
			TLVAmqp element = list.getList().get(5);
			if (!element.isNull())
				more = AMQPUnwrapper.unwrapBool(element);
		}
		if (size > 6) {
			TLVAmqp element = list.getList().get(6);
			if (!element.isNull())
				rcvSettleMode = ReceiveCodes.valueOf(AMQPUnwrapper.unwrapUByte(element));
		}
		if (size > 7) {
			TLVAmqp element = list.getList().get(7);
			if (!element.isNull()) {
				AMQPType code = element.getCode();
				if (code != AMQPType.LIST_0 && code != AMQPType.LIST_8 && code != AMQPType.LIST_32)
					throw new MalformedHeaderException("Expected type 'STATE' - received: "
							+ element.getCode());
				state = AMQPFactory.getState((TLVList) element);
				state.fill((TLVList) element);
			}
		}
		if (size > 8) {
			TLVAmqp element = list.getList().get(8);
			if (!element.isNull())
				resume = AMQPUnwrapper.unwrapBool(element);
		}
		if (size > 9) {
			TLVAmqp element = list.getList().get(9);
			if (!element.isNull())
				aborted = AMQPUnwrapper.unwrapBool(element);
		}
		if (size > 10) {
			TLVAmqp element = list.getList().get(10);
			if (!element.isNull())
				batchable = AMQPUnwrapper.unwrapBool(element);
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

	public Long getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(Long deliveryId) {
		this.deliveryId = deliveryId;
	}

	public byte[] getDeliveryTag() {
		return deliveryTag;
	}

	public void setDeliveryTag(byte[] deliveryTag) {
		this.deliveryTag = deliveryTag;
	}

	public AMQPMessageFormat getMessageFormat() {
		return messageFormat;
	}

	public void setMessageFormat(AMQPMessageFormat messageFormat) {
		this.messageFormat = messageFormat;
	}

	public Boolean getSettled() {
		return settled;
	}

	public void setSettled(Boolean settled) {
		this.settled = settled;
	}

	public Boolean getMore() {
		return more;
	}

	public void setMore(Boolean more) {
		this.more = more;
	}

	public ReceiveCodes getRcvSettleMode() {
		return rcvSettleMode;
	}

	public void setRcvSettleMode(ReceiveCodes rcvSettleMode) {
		this.rcvSettleMode = rcvSettleMode;
	}

	public AMQPState getState() {
		return state;
	}

	public void setState(AMQPState state) {
		this.state = state;
	}

	public Boolean getResume() {
		return resume;
	}

	public void setResume(Boolean resume) {
		this.resume = resume;
	}

	public Boolean getAborted() {
		return aborted;
	}

	public void setAborted(Boolean aborted) {
		this.aborted = aborted;
	}

	public Boolean getBatchable() {
		return batchable;
	}

	public void setBatchable(Boolean batchable) {
		this.batchable = batchable;
	}

	public AMQPSection getHeader() {
		return sections.get(SectionCodes.HEADER);
	}

	public AMQPSection getDeliveryAnnotations() {
		return sections.get(SectionCodes.DELIVERY_ANNOTATIONS);
	}

	public AMQPSection getMessageAnnotations() {
		return sections.get(SectionCodes.MESSAGE_ANNOTATIONS);
	}

	public AMQPSection getProperties() {
		return sections.get(SectionCodes.PROPERTIES);
	}

	public AMQPSection getApplicationProperties() {
		return sections.get(SectionCodes.APPLICATION_PROPERTIES);
	}

	public AMQPSection getData() {
		return sections.get(SectionCodes.DATA);
	}

	public AMQPSection getSequence() {
		return sections.get(SectionCodes.SEQUENCE);
	}

	public AMQPSection getValue() {
		return sections.get(SectionCodes.VALUE);
	}

	public AMQPSection getFooter() {
		return sections.get(SectionCodes.FOOTER);
	}

	public Map<SectionCodes, AMQPSection> getSections() {
		return sections;
	}

	public void addSections(AMQPSection... values) {
		if (sections == null)
			sections = new LinkedHashMap<SectionCodes, AMQPSection>();
		for (AMQPSection value : values)
			sections.put(value.getCode(), value);
	}

	@Override
	public int getLength() {
		int length = 8;
		TLVAmqp arguments = this.getArguments();
		length += arguments.getLength();

		ArrayList<AMQPSection> sectionsArray = new ArrayList<AMQPSection>(sections.values());
		for(AMQPSection item : sectionsArray) {
			length += item.getValue().getLength();
		}

		return length;
	}

	@Override
	public int getType() {
		return HeaderCodes.TRANSFER.getType();
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}
}
