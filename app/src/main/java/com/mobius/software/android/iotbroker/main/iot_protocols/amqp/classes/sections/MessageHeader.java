package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.SectionCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.DescribedConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPUnwrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPWrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVFixed;

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

public class MessageHeader extends AMQPSection {

	private Boolean durable;
	private Short priority;
	private Long milliseconds;
	private Boolean firstAquirer;
	private Long deliveryCount;

	@Override
	public TLVAmqp getValue() {

		TLVList list = new TLVList();

		if (durable != null)
			list.addElement(0, AMQPWrapper.wrap(durable));
		if (priority != null)
			list.addElement(1, AMQPWrapper.wrap(priority));
		if (milliseconds != null)
			list.addElement(2, AMQPWrapper.wrap(milliseconds));
		if (firstAquirer != null)
			list.addElement(3, AMQPWrapper.wrap(firstAquirer));
		if (deliveryCount != null)
			list.addElement(4, AMQPWrapper.wrap(deliveryCount));

		DescribedConstructor constructor = new DescribedConstructor(list.getCode(), new TLVFixed(
				AMQPType.SMALL_ULONG, new byte[] { 0x70 }));
		list.setConstructor(constructor);

		return list;
	}

	@Override
	public void fill(TLVAmqp value) {
		TLVList list = (TLVList) value;
		if (list.getList().size() > 0) {
			TLVAmqp element = list.getList().get(0);
			if (!element.isNull())
				durable = AMQPUnwrapper.unwrapBool(element);
		}
		if (list.getList().size() > 1) {
			TLVAmqp element = list.getList().get(1);
			if (!element.isNull())
				priority = AMQPUnwrapper.unwrapUByte(element);
		}
		if (list.getList().size() > 2) {
			TLVAmqp element = list.getList().get(2);
			if (!element.isNull())
				milliseconds = AMQPUnwrapper.unwrapUInt(element);
		}
		if (list.getList().size() > 3) {
			TLVAmqp element = list.getList().get(3);
			if (!element.isNull())
				firstAquirer = AMQPUnwrapper.unwrapBool(element);
		}
		if (list.getList().size() > 4) {
			TLVAmqp element = list.getList().get(4);
			if (!element.isNull())
				deliveryCount = AMQPUnwrapper.unwrapUInt(element);
		}
	}

	@Override
	public SectionCodes getCode() {
		return SectionCodes.HEADER;
	}

	public Boolean getDurable() {
		return durable;
	}

	public void setDurable(Boolean durable) {
		this.durable = durable;
	}

	public Short getPriority() {
		return priority;
	}

	public void setPriority(Short priority) {
		this.priority = priority;
	}

	public Long getMilliseconds() {
		return milliseconds;
	}

	public void setMilliseconds(Long milliseconds) {
		this.milliseconds = milliseconds;
	}

	public Boolean getFirstAquirer() {
		return firstAquirer;
	}

	public void setFirstAquirer(Boolean firstAquirer) {
		this.firstAquirer = firstAquirer;
	}

	public Long getDeliveryCount() {
		return deliveryCount;
	}

	public void setDeliveryCount(Long deliveryCount) {
		this.deliveryCount = deliveryCount;
	}

}
