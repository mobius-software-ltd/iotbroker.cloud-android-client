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

public class AMQPOpen extends AMQPHeader {

	private String containerId;
	private String hostname;
	private Long maxFrameSize;
	private Integer channelMax;
	private Long idleTimeout;
	private List<AMQPSymbol> outgoingLocales;
	private List<AMQPSymbol> incomingLocales;
	private List<AMQPSymbol> offeredCapabilities;
	private List<AMQPSymbol> desiredCapabilities;
	private Map<AMQPSymbol, Object> properties;

	public AMQPOpen() {
		super(HeaderCodes.OPEN);
	}

	@Override
	public TLVList getArguments() {
		TLVList list = new TLVList();

		if (containerId == null)
			throw new MalformedHeaderException("Open header's container id can't be null");
		list.addElement(0, AMQPWrapper.wrap(containerId));

		if (hostname != null)
			list.addElement(1, AMQPWrapper.wrap(hostname));
		if (maxFrameSize != null)
			list.addElement(2, AMQPWrapper.wrap(maxFrameSize));
		if (channelMax != null)
			list.addElement(3, AMQPWrapper.wrap(channelMax));
		if (idleTimeout != null)
			list.addElement(4, AMQPWrapper.wrap(idleTimeout));
		if (outgoingLocales != null)
			list.addElement(5, AMQPWrapper.wrapArray(outgoingLocales));
		if (incomingLocales != null)
			list.addElement(6, AMQPWrapper.wrapArray(incomingLocales));
		if (offeredCapabilities != null)
			list.addElement(7, AMQPWrapper.wrapArray(offeredCapabilities));
		if (desiredCapabilities != null)
			list.addElement(8, AMQPWrapper.wrapArray(desiredCapabilities));
		if (properties != null)
			list.addElement(9, AMQPWrapper.wrapMap(properties));

		DescribedConstructor constructor = new DescribedConstructor(list.getCode(),
				new TLVFixed(AMQPType.SMALL_ULONG, new byte[] { (byte) code.getType() }));
		list.setConstructor(constructor);

		return list;
	}

	@Override
	public void fillArguments(TLVList list) {

		int size = list.getList().size();

		if (size == 0)
			throw new MalformedHeaderException("Received malformed Open header: container id can't be null");

		if (size > 10)
			throw new MalformedHeaderException("Received malformed Open header. Invalid number of arguments: " + size);

		TLVAmqp element = list.getList().get(0);
		if (element.isNull())
			throw new MalformedHeaderException("Received malformed Open header: container id can't be null");
		containerId = AMQPUnwrapper.unwrapString(element);

		if (size > 1) {
			element = list.getList().get(1);
			if (!element.isNull())
				hostname = AMQPUnwrapper.unwrapString(element);
		}
		if (size > 2) {
			element = list.getList().get(2);
			if (!element.isNull())
				maxFrameSize = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 3) {
			element = list.getList().get(3);
			if (!element.isNull())
				channelMax = AMQPUnwrapper.unwrapUShort(element);
		}
		if (size > 4) {
			element = list.getList().get(4);
			if (!element.isNull())
				idleTimeout = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 5) {
			element = list.getList().get(5);
			if (!element.isNull())
				outgoingLocales = AMQPUnwrapper.unwrapArray(element);
		}
		if (size > 6) {
			element = list.getList().get(6);
			if (!element.isNull())
				incomingLocales = AMQPUnwrapper.unwrapArray(element);
		}
		if (size > 7) {
			element = list.getList().get(7);
			if (!element.isNull())
				offeredCapabilities = AMQPUnwrapper.unwrapArray(element);
		}
		if (size > 8) {
			element = list.getList().get(8);
			if (!element.isNull())
				desiredCapabilities = AMQPUnwrapper.unwrapArray(element);
		}
		if (size > 9) {
			element = list.getList().get(9);
			if (!element.isNull())
				properties = AMQPUnwrapper.unwrapMap(element);
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Doff: %d \n", doff));
		sb.append(String.format("Type: %d \n", headerType));
		sb.append(String.format("Channel: %d \n", channel));
		sb.append("Arguments: \n");
		sb.append(String.format("Container-id: %s \n", containerId));
		sb.append(String.format("Hostname: %s \n", hostname));
		sb.append(String.format("Max-frame-size: %s \n", maxFrameSize));
		sb.append(String.format("Channel max: %d \n", channelMax));
		sb.append(String.format("Idle-timeout: %d \n", idleTimeout));
		sb.append(String.format("Outgoing-locales (array of %d elements)", outgoingLocales.size()));
		sb.append(outgoingLocales);
		sb.append("\n");
		sb.append(String.format("Incoming-locales (array of %d elements)", incomingLocales.size()));
		sb.append(incomingLocales);
		sb.append("\n");
		sb.append(String.format("Offered capabilities (array of %d elements)", offeredCapabilities.size()));
		sb.append(offeredCapabilities);
		sb.append("\n");
		sb.append(String.format("Desired capabilities (array of %d elements)", desiredCapabilities.size()));
		sb.append(desiredCapabilities);
		sb.append("\n");
		sb.append(String.format("Properties (map of %d elements)", properties.size()));
		sb.append(properties);
		return sb.toString();
	}

	public String getContainerId() {
		return containerId;
	}

	public void setContainerId(String containerId) {
		if (containerId == null)
			throw new IllegalArgumentException("Container id can't be assigned a null value");
		this.containerId = containerId;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostName) {
		this.hostname = hostName;
	}

	public Long getMaxFrameSize() {
		return maxFrameSize;
	}

	public void setMaxFrameSize(Long maxFrameSize) {
		this.maxFrameSize = maxFrameSize;
	}

	public Integer getChannelMax() {
		return channelMax;
	}

	public void setChannelMax(Integer channelMax) {
		this.channelMax = channelMax;
	}

	public Long getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(Long idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public List<AMQPSymbol> getOutgoingLocales() {
		return outgoingLocales;
	}

	public void addOutgoingLocale(String... locales) {
		if (outgoingLocales == null)
			outgoingLocales = new ArrayList<AMQPSymbol>();
		for (String locale : locales)
			outgoingLocales.add(new AMQPSymbol(locale));
	}

	public List<AMQPSymbol> getIncomingLocales() {
		return incomingLocales;
	}

	public void addIncomingLocale(String... locales) {
		if (incomingLocales == null)
			incomingLocales = new ArrayList<AMQPSymbol>();
		for (String locale : locales)
			incomingLocales.add(new AMQPSymbol(locale));
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
		return HeaderCodes.OPEN.getType();
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}
}
