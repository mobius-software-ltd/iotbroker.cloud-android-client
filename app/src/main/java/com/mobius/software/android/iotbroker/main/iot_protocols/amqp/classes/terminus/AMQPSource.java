package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.terminus;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.DistributionMode;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.TerminusDurability;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.TerminusExpiryPolicy;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.DescribedConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.exceptions.MalformedHeaderException;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPFactory;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPUnwrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPWrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described.AMQPOutcome;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVFixed;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.AMQPSymbol;

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

public class AMQPSource {

	private String address;
	private TerminusDurability durable;
	private TerminusExpiryPolicy expiryPeriod;
	private Long timeout;
	private Boolean dynamic;
	private Map<AMQPSymbol, Object> dynamicNodeProperties;
	private DistributionMode distributionMode;
	private Map<AMQPSymbol, Object> filter;
	private AMQPOutcome defaultOutcome;
	private List<AMQPSymbol> outcomes;
	private List<AMQPSymbol> capabilities;

	public TLVList getList() {

		TLVList list = new TLVList();

		if (address != null)
			list.addElement(0, AMQPWrapper.wrap(address));
		if (durable != null)
			list.addElement(1, AMQPWrapper.wrap(durable.getCode()));
		if (expiryPeriod != null)
			list.addElement(2, AMQPWrapper.wrap(new AMQPSymbol(expiryPeriod.getPolicy())));
		if (timeout != null)
			list.addElement(3, AMQPWrapper.wrap(timeout));
		if (dynamic != null)
			list.addElement(4, AMQPWrapper.wrap(dynamic));

		if (dynamicNodeProperties != null)
			if (dynamic != null) {
				if (dynamic)
					list.addElement(5, AMQPWrapper.wrapMap(dynamicNodeProperties));
				else
					throw new MalformedHeaderException(
							"Source's dynamic-node-properties can't be specified when dynamic flag is false");
			} else
				throw new MalformedHeaderException(
						"Source's dynamic-node-properties can't be specified when dynamic flag is not set");

		if (distributionMode != null)
			list.addElement(6, AMQPWrapper.wrap(new AMQPSymbol(distributionMode.getMode())));
		if (filter != null)
			list.addElement(7, AMQPWrapper.wrapMap(filter));
		if (defaultOutcome != null)
			list.addElement(8, defaultOutcome.getList());
		if (outcomes != null)
			list.addElement(9, AMQPWrapper.wrapArray(outcomes));
		if (capabilities != null)
			list.addElement(10, AMQPWrapper.wrapArray(capabilities));

		DescribedConstructor constructor = new DescribedConstructor(list.getCode(),
				new TLVFixed(AMQPType.SMALL_ULONG, new byte[] { 0x28 }));
		list.setConstructor(constructor);

		return list;
	}

	public void fill(TLVList list) {

		if (list.getList().size() > 0) {
			TLVAmqp element = list.getList().get(0);
			if (!element.isNull())
				address = AMQPUnwrapper.unwrapString(element);
		}
		if (list.getList().size() > 1) {
			TLVAmqp element = list.getList().get(1);
			if (!element.isNull())
				durable = TerminusDurability.valueOf(AMQPUnwrapper.unwrapUInt(element));
		}
		if (list.getList().size() > 2) {
			TLVAmqp element = list.getList().get(2);
			if (!element.isNull())
				expiryPeriod = TerminusExpiryPolicy.getPolicy(AMQPUnwrapper.unwrapSymbol(element).getValue());
		}
		if (list.getList().size() > 3) {
			TLVAmqp element = list.getList().get(3);
			if (!element.isNull())
				timeout = AMQPUnwrapper.unwrapUInt(element);
		}
		if (list.getList().size() > 4) {
			TLVAmqp element = list.getList().get(4);
			if (!element.isNull())
				dynamic = AMQPUnwrapper.unwrapBool(element);
		}
		if (list.getList().size() > 5) {
			TLVAmqp element = list.getList().get(5);
			if (!element.isNull()) {
				if (dynamic != null) {
					if (dynamic)
						dynamicNodeProperties = AMQPUnwrapper.unwrapMap(element);
					else
						throw new MalformedHeaderException(
								"Received malformed Source: dynamic-node-properties can't be specified when dynamic flag is false");
				} else
					throw new MalformedHeaderException(
							"Received malformed Source: dynamic-node-properties can't be specified when dynamic flag is not set");
			}
		}
		if (list.getList().size() > 6) {
			TLVAmqp element = list.getList().get(6);
			if (!element.isNull())
				distributionMode = DistributionMode.getMode(AMQPUnwrapper.unwrapSymbol(element).getValue());
		}
		if (list.getList().size() > 7) {
			TLVAmqp element = list.getList().get(7);
			if (!element.isNull())
				filter = AMQPUnwrapper.unwrapMap(element);
		}
		if (list.getList().size() > 8) {
			TLVAmqp element = list.getList().get(8);
			if (!element.isNull()) {
				AMQPType code = element.getCode();
				if (code != AMQPType.LIST_0 && code != AMQPType.LIST_8 && code != AMQPType.LIST_32)
					throw new MalformedHeaderException("Expected type 'OUTCOME' - received: " + element.getCode());
				defaultOutcome = AMQPFactory.getOutcome((TLVList) element);
				defaultOutcome.fill((TLVList) element);
			}
		}
		if (list.getList().size() > 9) {
			TLVAmqp element = list.getList().get(9);
			if (!element.isNull())
				outcomes = AMQPUnwrapper.unwrapArray(element);
		}
		if (list.getList().size() > 10) {
			TLVAmqp element = list.getList().get(10);
			if (!element.isNull())
				capabilities = AMQPUnwrapper.unwrapArray(element);
		}
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public TerminusDurability getDurable() {
		return durable;
	}

	public void setDurable(TerminusDurability durability) {
		this.durable = durability;
	}

	public TerminusExpiryPolicy getExpiryPeriod() {
		return expiryPeriod;
	}

	public void setExpiryPeriod(TerminusExpiryPolicy expiryPeriod) {
		this.expiryPeriod = expiryPeriod;
	}

	public Long getTimeout() {
		return timeout;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}

	public Boolean getDynamic() {
		return dynamic;
	}

	public void setDynamic(Boolean dynamic) {
		this.dynamic = dynamic;
	}

	public Map<AMQPSymbol, Object> getDynamicNodeProperties() {
		return dynamicNodeProperties;
	}

	public void addDynamicNodeProperty(String key, Object value) {
		if (dynamicNodeProperties == null)
			dynamicNodeProperties = new LinkedHashMap<AMQPSymbol, Object>();
		dynamicNodeProperties.put(new AMQPSymbol(key), value);
	}

	public DistributionMode getDistributionMode() {
		return distributionMode;
	}

	public void setDistributionMode(DistributionMode distributionMode) {
		this.distributionMode = distributionMode;
	}

	public Map<AMQPSymbol, Object> getFilter() {
		return filter;
	}

	public void addFilter(String key, Object value) {
		if (filter == null)
			filter = new LinkedHashMap<AMQPSymbol, Object>();
		filter.put(new AMQPSymbol(key), value);
	}

	public AMQPOutcome getDefaultOutcome() {
		return defaultOutcome;
	}

	public void setDefaultOutcome(AMQPOutcome defaultOutcome) {
		this.defaultOutcome = defaultOutcome;
	}

	public List<AMQPSymbol> getOutcomes() {
		return outcomes;
	}

	public void addOutcomes(String... values) {
		if (outcomes == null)
			outcomes = new ArrayList<AMQPSymbol>();
		for (String value : values)
			outcomes.add(new AMQPSymbol(value));
	}

	public List<AMQPSymbol> getCapabilities() {
		return capabilities;
	}

	public void addCapabilities(String... values) {
		if (capabilities == null)
			capabilities = new ArrayList<AMQPSymbol>();
		for (String value : values)
			capabilities.add(new AMQPSymbol(value));
	}

}
