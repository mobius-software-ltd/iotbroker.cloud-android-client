package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.ErrorCodes;
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

public class AMQPError {

	private ErrorCodes condition;
	private String description;
	private Map<AMQPSymbol, Object> info;

	public TLVList getList() {

		TLVList list = new TLVList();

		if (condition != null)
			list.addElement(0, AMQPWrapper.wrap(new AMQPSymbol(condition.getCondition())));

		if (description != null)
			list.addElement(1, AMQPWrapper.wrap(description));

		if (info != null)
			list.addElement(2, AMQPWrapper.wrapMap(info));

		DescribedConstructor constructor = new DescribedConstructor(list.getCode(), new TLVFixed(
				AMQPType.SMALL_ULONG, new byte[] { 0x1D }));

		list.setConstructor(constructor);

		return list;
	}

	public void fill(TLVList list) {

		if (list.getList().size() > 0) {
			TLVAmqp element = list.getList().get(0);
			if (!element.isNull())
				condition = ErrorCodes.getCondition(AMQPUnwrapper.unwrapSymbol(element).getValue());
		}

		if (list.getList().size() > 1) {
			TLVAmqp element = list.getList().get(1);
			if (!element.isNull())
				description = AMQPUnwrapper.unwrapString(element);
		}

		if (list.getList().size() > 2) {
			TLVAmqp element = list.getList().get(2);
			if (!element.isNull())
				info = AMQPUnwrapper.unwrapMap(element);
		}

	}

	public ErrorCodes getCondition() {
		return condition;
	}

	public void setCondition(ErrorCodes condition) {
		this.condition = condition;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Map<AMQPSymbol, Object> getInfo() {
		return info;
	}

	public void addInfo(String key, Object value) {
		if (info == null)
			info = new LinkedHashMap<AMQPSymbol, Object>();
		info.put(new AMQPSymbol(key), value);
	}

}
