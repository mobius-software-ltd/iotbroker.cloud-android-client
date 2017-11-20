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
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVFixed;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.AMQPSymbol;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;

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

public class AMQPFlow extends AMQPHeader {

	private Long nextIncomingId;
	private Long incomingWindow;
	private Long nextOutgoingId;
	private Long outgoingWindow;
	private Long handle;
	private Long deliveryCount;
	private Long linkCredit;
	private Long avaliable;
	private Boolean drain;
	private Boolean echo;
	private Map<AMQPSymbol, Object> properties;

	public AMQPFlow() {
		super(HeaderCodes.FLOW);
	}

	@Override
	public TLVList getArguments() {

		TLVList list = new TLVList();

		if (nextIncomingId != null)
			list.addElement(0, AMQPWrapper.wrap(nextIncomingId));

		if (incomingWindow == null)
			throw new MalformedHeaderException("Flow header's incoming-window can't be null");
		list.addElement(1, AMQPWrapper.wrap(incomingWindow));

		if (nextOutgoingId == null)
			throw new MalformedHeaderException("Flow header's next-outgoing-id can't be null");
		list.addElement(2, AMQPWrapper.wrap(nextOutgoingId));

		if (outgoingWindow == null)
			throw new MalformedHeaderException("Flow header's outgoing-window can't be null");
		list.addElement(3, AMQPWrapper.wrap(outgoingWindow));

		if (handle != null)
			list.addElement(4, AMQPWrapper.wrap(handle));

		if (deliveryCount != null)
			if (handle != null)
				list.addElement(5, AMQPWrapper.wrap(deliveryCount));
			else
				throw new MalformedHeaderException(
						"Flow headers delivery-count can't be assigned when handle is not specified");

		if (linkCredit != null)
			if (handle != null)
				list.addElement(6, AMQPWrapper.wrap(linkCredit));
			else
				throw new MalformedHeaderException(
						"Flow headers link-credit can't be assigned when handle is not specified");

		if (avaliable != null)
			if (handle != null)
				list.addElement(7, AMQPWrapper.wrap(avaliable));
			else
				throw new MalformedHeaderException(
						"Flow headers avaliable can't be assigned when handle is not specified");

		if (drain != null)
			if (handle != null)
				list.addElement(8, AMQPWrapper.wrap(drain));
			else
				throw new MalformedHeaderException("Flow headers drain can't be assigned when handle is not specified");

		if (echo != null)
			list.addElement(9, AMQPWrapper.wrap(echo));
		if (properties != null)
			list.addElement(10, AMQPWrapper.wrapMap(properties));

		DescribedConstructor constructor = new DescribedConstructor(list.getCode(),
				new TLVFixed(AMQPType.SMALL_ULONG, new byte[] { (byte) code.getType() }));
		list.setConstructor(constructor);

		return list;
	}

	@Override
	public void fillArguments(TLVList list) {

		int size = list.getList().size();

		if (size < 4)
			throw new MalformedHeaderException("Received malformed Flow header: mandatory "
					+ "fields incoming-window, next-outgoing-id and " + "outgoing-window must not be null");

		if (size > 11)
			throw new MalformedHeaderException("Received malformed Flow header. Invalid arguments size: " + size);

		if (size > 0) {
			TLVAmqp element = list.getList().get(0);
			if (!element.isNull())
				nextIncomingId = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 1) {
			TLVAmqp element = list.getList().get(1);
			if (element.isNull())
				throw new MalformedHeaderException("Received malformed Flow header: incoming-window can't be null");
			incomingWindow = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 2) {
			TLVAmqp element = list.getList().get(2);
			if (element.isNull())
				throw new MalformedHeaderException("Received malformed Flow header: next-outgoing-id can't be null");
			nextOutgoingId = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 3) {
			TLVAmqp element = list.getList().get(3);
			if (element.isNull())
				throw new MalformedHeaderException("Received malformed Flow header: outgoing-window can't be null");
			outgoingWindow = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 4) {
			TLVAmqp element = list.getList().get(4);
			if (!element.isNull())
				handle = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 5) {
			TLVAmqp element = list.getList().get(5);
			if (!element.isNull())
				if (handle != null)
					deliveryCount = AMQPUnwrapper.unwrapUInt(element);
				else
					throw new MalformedHeaderException(
							"Received malformed Flow header: delivery-count can't be present when handle is null");
		}
		if (size > 6) {
			TLVAmqp element = list.getList().get(6);
			if (!element.isNull())
				if (handle != null)
					linkCredit = AMQPUnwrapper.unwrapUInt(element);
				else
					throw new MalformedHeaderException(
							"Received malformed Flow header: link-credit can't be present when handle is null");

		}
		if (size > 7) {
			TLVAmqp element = list.getList().get(7);
			if (!element.isNull())
				if (handle != null)
					avaliable = AMQPUnwrapper.unwrapUInt(element);
				else
					throw new MalformedHeaderException(
							"Received malformed Flow header: avaliable can't be present when handle is null");
		}
		if (size > 8) {
			TLVAmqp element = list.getList().get(8);
			if (!element.isNull())
				if (handle != null)
					drain = AMQPUnwrapper.unwrapBool(element);
				else
					throw new MalformedHeaderException(
							"Received malformed Flow header: drain can't be present when handle is null");

		}
		if (size > 9) {
			TLVAmqp element = list.getList().get(9);
			if (!element.isNull())
				echo = AMQPUnwrapper.unwrapBool(element);
		}
		if (size > 10) {
			TLVAmqp element = list.getList().get(10);
			if (!element.isNull())
				properties = AMQPUnwrapper.unwrapMap(element);
		}

	}

	public Long getNextIncomingId() {
		return nextIncomingId;
	}

	public void setNextIncomingId(Long nextIncomingId) {
		this.nextIncomingId = nextIncomingId;
	}

	public Long getIncomingWindow() {
		return incomingWindow;
	}

	public void setIncomingWindow(Long incomingWindow) {
		if (incomingWindow == null)
			throw new IllegalArgumentException("Incoming-window can't be assigned a null value");
		this.incomingWindow = incomingWindow;
	}

	public Long getNextOutgoingId() {
		return nextOutgoingId;
	}

	public void setNextOutgoingId(Long nextOutgoingId) {
		if (nextOutgoingId == null)
			throw new IllegalArgumentException("Next-outgoing-id can't be assigned a null value");
		this.nextOutgoingId = nextOutgoingId;
	}

	public Long getOutgoingWindow() {
		return outgoingWindow;
	}

	public void setOutgoingWindow(Long outgoingWindow) {
		if (outgoingWindow == null)
			throw new IllegalArgumentException("Outgoing-window can't be assigned a null value");
		this.outgoingWindow = outgoingWindow;
	}

	public Long getHandle() {
		return handle;
	}

	public void setHandle(Long handle) {
		this.handle = handle;
	}

	public Long getDeliveryCount() {
		return deliveryCount;
	}

	public void setDeliveryCount(Long deliveryCount) {
		this.deliveryCount = deliveryCount;
	}

	public Long getLinkCredit() {
		return linkCredit;
	}

	public void setLinkCredit(Long linkCredit) {
		this.linkCredit = linkCredit;
	}

	public Long getAvaliable() {
		return avaliable;
	}

	public void setAvaliable(Long avaliable) {
		this.avaliable = avaliable;
	}

	public Boolean getDrain() {
		return drain;
	}

	public void setDrain(Boolean drain) {
		this.drain = drain;
	}

	public Boolean getEcho() {
		return echo;
	}

	public void setEcho(Boolean echo) {
		this.echo = echo;
	}

	public Map<AMQPSymbol, Object> getProperties() {
		return properties;
	}

	public void addProperty(String key, Object value) {
		if (properties == null)
			properties = new LinkedHashMap<AMQPSymbol, Object>();
		properties.put(new AMQPSymbol(key), value);
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
		return HeaderCodes.FLOW.getType();
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}
}
