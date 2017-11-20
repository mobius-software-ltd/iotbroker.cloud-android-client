package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.DescribedConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPUnwrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPWrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVFixed;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.AMQPSymbol;

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

public class AMQPModified implements AMQPOutcome, AMQPState {

	private Boolean deliveryFailed;
	private Boolean undeliverableHere;
	private Map<AMQPSymbol, Object> messageAnnotations;

	@Override
	public TLVList getList() {

		TLVList list = new TLVList();

		if (deliveryFailed != null)
			list.addElement(0, AMQPWrapper.wrap(deliveryFailed));
		if (undeliverableHere != null)
			list.addElement(1, AMQPWrapper.wrap(undeliverableHere));
		if (messageAnnotations != null)
			list.addElement(2, AMQPWrapper.wrapMap(messageAnnotations));

		DescribedConstructor constructor = new DescribedConstructor(list.getCode(), new TLVFixed(
				AMQPType.SMALL_ULONG, new byte[] { 0x27 }));
		list.setConstructor(constructor);

		return list;
	}

	@Override
	public void fill(TLVList list) {
		if (list.getList().size() > 0) {
			TLVAmqp element = list.getList().get(0);
			if (!element.isNull())
				deliveryFailed = AMQPUnwrapper.unwrapBool(element);
		}
		if (list.getList().size() > 1) {
			TLVAmqp element = list.getList().get(1);
			if (!element.isNull())
				undeliverableHere = AMQPUnwrapper.unwrapBool(element);
		}
		if (list.getList().size() > 2) {
			TLVAmqp element = list.getList().get(2);
			if (!element.isNull())
				messageAnnotations = AMQPUnwrapper.unwrapMap(element);
		}
	}

	public Boolean getDeliveryFailed() {
		return deliveryFailed;
	}

	public void setDeliveryFailed(Boolean deliveryFailed) {
		this.deliveryFailed = deliveryFailed;
	}

	public Boolean getUndeliverableHere() {
		return undeliverableHere;
	}

	public void setUndeliverableHere(Boolean undeliverableHere) {
		this.undeliverableHere = undeliverableHere;
	}

	public Map<AMQPSymbol, Object> getMessageAnnotations() {
		return messageAnnotations;
	}

	public void addMessageAnnotation(String key, Object value) {
		if (!key.startsWith("x-"))
			throw new IllegalArgumentException();
		if (messageAnnotations == null)
			messageAnnotations = new LinkedHashMap<AMQPSymbol, Object>();
		messageAnnotations.put(new AMQPSymbol(key), value);
	}
}
