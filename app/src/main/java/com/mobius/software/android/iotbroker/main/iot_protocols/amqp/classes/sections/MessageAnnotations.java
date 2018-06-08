package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections;

import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.SectionCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.DescribedConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPUnwrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPWrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVMap;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVFixed;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.wrappers.AMQPSymbol;

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

public class MessageAnnotations extends AMQPSection {

	private Map<Object, Object> annotations;

	@Override
	public TLVAmqp getValue() {

		TLVMap map = new TLVMap();

		if (annotations != null)
			map = AMQPWrapper.wrapMap(annotations);

		DescribedConstructor constructor = new DescribedConstructor(map.getCode(), new TLVFixed(
				AMQPType.SMALL_ULONG, new byte[] { 0x72 }));
		map.setConstructor(constructor);

		return map;
	}

	@Override
	public void fill(TLVAmqp map) {
		if (!map.isNull())
			annotations = AMQPUnwrapper.unwrapMap(map);
	}

	@Override
	public SectionCodes getCode() {
		return SectionCodes.MESSAGE_ANNOTATIONS;
	}

	public Map<Object, Object> getAnnotations() {
		return annotations;
	}

	@SuppressWarnings("unchecked")
	public void addAnnotation(Object key, Object value) {
		if (annotations == null)
			annotations = new LinkedHashMap<>();
		if (key instanceof String)
			annotations.put(new AMQPSymbol((String) key), value);
		else if (key instanceof BigInteger)
			annotations.put(key, value);
		else
			throw new IllegalArgumentException(
					"DeliveryAnnotations keys are restricted to types Symbol and ULong. Received key type: "
							+ key.getClass());
	}

}
