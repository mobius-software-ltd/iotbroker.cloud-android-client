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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

public class AMQPBegin extends AMQPHeader {

	private Integer remoteChannel;
	private Long nextOutgoingId;
	private Long incomingWindow;
	private Long outgoingWindow;
	private Long handleMax;
	private List<AMQPSymbol> offeredCapabilities;
	private List<AMQPSymbol> desiredCapabilities;
	private Map<AMQPSymbol, Object> properties;

	public AMQPBegin() {
		super(HeaderCodes.BEGIN);
	}

	@Override
	public TLVList getArguments() {

		TLVList list = new TLVList();

		if (remoteChannel != null)
			list.addElement(0, AMQPWrapper.wrap(remoteChannel));

		if (nextOutgoingId == null)
			throw new MalformedHeaderException("Begin header's next-outgoing-id can't be null");
		list.addElement(1, AMQPWrapper.wrap(nextOutgoingId));

		if (incomingWindow == null)
			throw new MalformedHeaderException("Begin header's incoming-window can't be null");
		list.addElement(2, AMQPWrapper.wrap(incomingWindow));

		if (outgoingWindow == null)
			throw new MalformedHeaderException("Begin header's incoming-window can't be null");
		list.addElement(3, AMQPWrapper.wrap(outgoingWindow));

		if (handleMax != null)
			list.addElement(4, AMQPWrapper.wrap(handleMax));
		if (offeredCapabilities != null)
			list.addElement(5, AMQPWrapper.wrapArray(offeredCapabilities));
		if (desiredCapabilities != null)
			list.addElement(6, AMQPWrapper.wrapArray(desiredCapabilities));
		if (properties != null)
			list.addElement(7, AMQPWrapper.wrapMap(properties));

		DescribedConstructor constructor = new DescribedConstructor(list.getCode(),
				new TLVFixed(AMQPType.SMALL_ULONG, new byte[] { (byte) code.getType() }));
		list.setConstructor(constructor);

		return list;
	}

	@Override
	public void fillArguments(TLVList list) {

		int size = list.getList().size();

		if (size < 4)
			throw new MalformedHeaderException("Received malformed Begin header: mandatory "
					+ "fields next-outgoing-id, incoming-window and " + "outgoing-window must not be null");

		if (size > 8)
			throw new MalformedHeaderException("Received malformed Begin header. Invalid number of arguments: " + size);

		if (size > 0) {
			TLVAmqp element = list.getList().get(0);
			if (!element.isNull())
				remoteChannel = AMQPUnwrapper.unwrapUShort(element);
		}
		if (size > 1) {
			TLVAmqp element = list.getList().get(1);
			if (element.isNull())
				throw new MalformedHeaderException("Received malformed Begin header: next-outgoing-id can't be null");
			nextOutgoingId = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 2) {
			TLVAmqp element = list.getList().get(2);
			if (element.isNull())
				throw new MalformedHeaderException("Received malformed Begin header: incoming-window can't be null");
			incomingWindow = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 3) {
			TLVAmqp element = list.getList().get(3);
			if (element.isNull())
				throw new MalformedHeaderException("Received malformed Begin header: outgoing-window can't be null");
			outgoingWindow = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 4) {
			TLVAmqp element = list.getList().get(4);
			if (!element.isNull())
				handleMax = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 5) {
			TLVAmqp element = list.getList().get(5);
			if (!element.isNull())
				offeredCapabilities = AMQPUnwrapper.unwrapArray(element);
		}
		if (size > 6) {
			TLVAmqp element = list.getList().get(6);
			if (!element.isNull())
				desiredCapabilities = AMQPUnwrapper.unwrapArray(element);
		}
		if (size > 7) {
			TLVAmqp element = list.getList().get(7);
			if (!element.isNull())
				properties = AMQPUnwrapper.unwrapMap(element);
		}
	}

	public Integer getRemoteChannel() {
		return remoteChannel;
	}

	public void setRemoteChannel(Integer remoteChannel) {
		this.remoteChannel = remoteChannel;
	}

	public Long getNextOutgoingId() {
		return nextOutgoingId;
	}

	public void setNextOutgoingId(Long nextOutgoingId) {
		if (nextOutgoingId == null)
			throw new IllegalArgumentException("Next-outgoing-id id can't be assigned a null value");
		this.nextOutgoingId = nextOutgoingId;
	}

	public Long getIncomingWindow() {
		return incomingWindow;
	}

	public void setIncomingWindow(Long incomingWindow) {
		if (incomingWindow == null)
			throw new IllegalArgumentException("Incoming-window id can't be assigned a null value");
		this.incomingWindow = incomingWindow;
	}

	public Long getOutgoingWindow() {
		return outgoingWindow;
	}

	public void setOutgoingWindow(Long outgoingWindow) {
		if (outgoingWindow == null)
			throw new IllegalArgumentException("Outgoing-window id can't be assigned a null value");
		this.outgoingWindow = outgoingWindow;
	}

	public Long getHandleMax() {
		return handleMax;
	}

	public void setHandleMax(Long handleMax) {
		this.handleMax = handleMax;
	}

	public List<AMQPSymbol> getOfferedCapabilities() {
		return offeredCapabilities;
	}

	public void addOfferedCapability(String... capabilities) {
		if (offeredCapabilities == null)
			offeredCapabilities = new ArrayList<AMQPSymbol>();
		for (String capability : capabilities)
			offeredCapabilities.add(new AMQPSymbol(capability));
	}

	public List<AMQPSymbol> getDesiredCapabilities() {
		return desiredCapabilities;
	}

	public void addDesiredCapability(String... capabilities) {
		if (desiredCapabilities == null)
			desiredCapabilities = new ArrayList<AMQPSymbol>();
		for (String capability : capabilities)
			desiredCapabilities.add(new AMQPSymbol(capability));
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
		return HeaderCodes.BEGIN.getType();
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}
}
