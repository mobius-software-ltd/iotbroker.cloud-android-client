package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.HeaderCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.ReceiveCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.RoleCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.SendCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.DescribedConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.exceptions.MalformedHeaderException;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPUnwrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPWrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.terminus.AMQPSource;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.terminus.AMQPTarget;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVFixed;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.AMQPSymbol;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;

import java.math.BigInteger;
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

public class AMQPAttach extends AMQPHeader {

	private String name;
	private Long handle;
	private RoleCodes role;
	private SendCodes sndSettleMode;
	private ReceiveCodes rcvSettleMode;
	private AMQPSource source;
	private AMQPTarget target;
	private Map<AMQPSymbol, Object> unsettled;
	private Boolean incompleteUnsettled;
	private Long initialDeliveryCount;
	private BigInteger maxMessageSize;
	private List<AMQPSymbol> offeredCapabilities;
	private List<AMQPSymbol> desiredCapabilities;
	private Map<AMQPSymbol, Object> properties;

	public AMQPAttach() {
		super(HeaderCodes.ATTACH);
	}

	@Override
	public TLVList getArguments() {

		TLVList list = new TLVList();

		if (name == null)
			throw new MalformedHeaderException("Attach header's name can't be null");
		list.addElement(0, AMQPWrapper.wrapString(name));

		if (handle == null)
			throw new MalformedHeaderException("Attach header's handle can't be null");
		list.addElement(1, AMQPWrapper.wrap(handle));

		if (role == null)
			throw new MalformedHeaderException("Attach header's role can't be null");
		list.addElement(2, AMQPWrapper.wrap(role.getType()));

		if (sndSettleMode != null)
			list.addElement(3, AMQPWrapper.wrap(sndSettleMode.getType()));
		if (rcvSettleMode != null)
			list.addElement(4, AMQPWrapper.wrap(rcvSettleMode.getType()));
		if (source != null)
			list.addElement(5, source.getList());
		if (target != null)
			list.addElement(6, target.getList());
		if (unsettled != null)
			list.addElement(7, AMQPWrapper.wrapMap(unsettled));
		if (incompleteUnsettled != null)
			list.addElement(8, AMQPWrapper.wrap(incompleteUnsettled));

		if (initialDeliveryCount != null)
			list.addElement(9, AMQPWrapper.wrap(initialDeliveryCount));
		else if (role.equals(RoleCodes.SENDER))
			throw new MalformedHeaderException(
					"Sender's attach header must contain a non-null initial-delivery-count value");

		if (maxMessageSize != null)
			list.addElement(10, AMQPWrapper.wrap(maxMessageSize));
		if (offeredCapabilities != null)
			list.addElement(11, AMQPWrapper.wrapArray(offeredCapabilities));
		if (desiredCapabilities != null)
			list.addElement(12, AMQPWrapper.wrapArray(desiredCapabilities));
		if (properties != null)
			list.addElement(13, AMQPWrapper.wrapMap(properties));

		DescribedConstructor constructor = new DescribedConstructor(list.getCode(),
				new TLVFixed(AMQPType.SMALL_ULONG, new byte[] { (byte) code.getType() }));

		list.setConstructor(constructor);

		return list;
	}

	@Override
	public void fillArguments(TLVList list) {

		int size = list.getList().size();

		if (size < 3)
			throw new MalformedHeaderException(
					"Received malformed Attach header: mandatory " + "fields name, handle and role must not be null");

		if (size > 14)
			throw new MalformedHeaderException(
					"Received malformed Attach header. Invalid number of arguments: " + size);

		if (size > 0) {
			TLVAmqp element = list.getList().get(0);
			if (element.isNull())
				throw new MalformedHeaderException("Received malformed Attach header: name can't be null");
			name = AMQPUnwrapper.unwrapString(element);
		}
		if (size > 1) {
			TLVAmqp element = list.getList().get(1);
			if (element.isNull())
				throw new MalformedHeaderException("Received malformed Attach header: handle can't be null");
			handle = AMQPUnwrapper.unwrapUInt(element);
		}
		if (size > 2) {
			TLVAmqp element = list.getList().get(2);
			if (element.isNull())
				throw new MalformedHeaderException("Received malformed Attach header: role can't be null");
			role = RoleCodes.valueOf(AMQPUnwrapper.unwrapBool(element));
		}
		if (size > 3) {
			TLVAmqp element = list.getList().get(3);
			if (!element.isNull())
				sndSettleMode = SendCodes.valueOf(AMQPUnwrapper.unwrapUByte(element));
		}
		if (size > 4) {
			TLVAmqp element = list.getList().get(4);
			if (!element.isNull())
				rcvSettleMode = ReceiveCodes.valueOf(AMQPUnwrapper.unwrapUByte(element));
		}
		if (size > 5) {
			TLVAmqp element = list.getList().get(5);
			if (!element.isNull()) {
				AMQPType code = element.getCode();
				if (code != AMQPType.LIST_0 && code != AMQPType.LIST_8 && code != AMQPType.LIST_32)
					throw new MalformedHeaderException("Expected type SOURCE - received: " + element.getCode());
				source = new AMQPSource();
				source.fill((TLVList) element);
			}
		}
		if (size > 6) {
			TLVAmqp element = list.getList().get(6);
			if (!element.isNull()) {
				AMQPType code = element.getCode();
				if (code != AMQPType.LIST_0 && code != AMQPType.LIST_8 && code != AMQPType.LIST_32)
					throw new MalformedHeaderException("Expected type TARGET - received: " + element.getCode());
				target = new AMQPTarget();
				target.fill((TLVList) element);
			}
		}
		if (size > 7) {
			TLVAmqp unsettledMap = list.getList().get(7);
			if (!unsettledMap.isNull())
				unsettled = AMQPUnwrapper.unwrapMap(unsettledMap);
		}
		if (size > 8) {
			TLVAmqp element = list.getList().get(8);
			if (!element.isNull())
				incompleteUnsettled = AMQPUnwrapper.unwrapBool(element);
		}
		if (size > 9) {
			TLVAmqp element = list.getList().get(9);
			if (!element.isNull())
				initialDeliveryCount = AMQPUnwrapper.unwrapUInt(element);
			else if (role.equals(RoleCodes.SENDER))
				throw new MalformedHeaderException("Received an attach header with a null initial-delivery-count");
		}
		if (size > 10) {
			TLVAmqp element = list.getList().get(10);
			if (!element.isNull())
				maxMessageSize = AMQPUnwrapper.unwrapULong(element);
		}
		if (size > 11) {
			TLVAmqp element = list.getList().get(11);
			if (!element.isNull())
				offeredCapabilities = AMQPUnwrapper.unwrapArray(element);
		}
		if (size > 12) {
			TLVAmqp element = list.getList().get(12);
			if (!element.isNull())
				desiredCapabilities = AMQPUnwrapper.unwrapArray(element);
		}
		if (size > 13) {
			TLVAmqp element = list.getList().get(13);
			if (!element.isNull())
				properties = AMQPUnwrapper.unwrapMap(element);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null)
			throw new IllegalArgumentException("Name can't be assigned a null value");
		this.name = name;
	}

	public Long getHandle() {
		return handle;
	}

	public void setHandle(Long handle) {
		if (handle == null)
			throw new IllegalArgumentException("Handle can't be assigned a null value");
		this.handle = handle;
	}

	public RoleCodes getRole() {
		return role;
	}

	public void setRole(RoleCodes role) {
		if (role == null)
			throw new IllegalArgumentException("Role can't be assigned a null value");
		this.role = role;
	}

	public SendCodes getSndSettleMode() {
		return sndSettleMode;
	}

	public void setSndSettleMode(SendCodes sndSettleMode) {
		this.sndSettleMode = sndSettleMode;
	}

	public ReceiveCodes getRcvSettleMode() {
		return rcvSettleMode;
	}

	public void setRcvSettleMode(ReceiveCodes rcvSettleMode) {
		this.rcvSettleMode = rcvSettleMode;
	}

	public AMQPSource getSource() {
		return source;
	}

	public void setSource(AMQPSource source) {
		this.source = source;
	}

	public AMQPTarget getTarget() {
		return target;
	}

	public void setTarget(AMQPTarget target) {
		this.target = target;
	}

	public Map<AMQPSymbol, Object> getUnsettled() {
		return unsettled;
	}

	public void addUnsettled(String key, Object value) {
		if (key == null)
			throw new IllegalArgumentException("Unsettled map cannot contain objects with null keys");
		if (unsettled == null)
			unsettled = new LinkedHashMap<AMQPSymbol, Object>();
		unsettled.put(new AMQPSymbol(key), value);
	}

	public Boolean getIncompleteUnsettled() {
		return incompleteUnsettled;
	}

	public void setIncompleteUnsettled(Boolean incompleteUnsettled) {
		this.incompleteUnsettled = incompleteUnsettled;
	}

	public Long getInitialDeliveryCount() {
		return initialDeliveryCount;
	}

	public void setInitialDeliveryCount(Long initialDeliveryCount) {
		this.initialDeliveryCount = initialDeliveryCount;
	}

	public BigInteger getMaxMessageSize() {
		return maxMessageSize;
	}

	public void setMaxMessageSize(BigInteger maxMessageSize) {
		this.maxMessageSize = maxMessageSize;
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
		return HeaderCodes.ATTACH.getType();
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}
}
