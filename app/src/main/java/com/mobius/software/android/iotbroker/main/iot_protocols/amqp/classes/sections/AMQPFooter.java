package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.SectionCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.DescribedConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPUnwrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPWrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVMap;
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

public class AMQPFooter extends AMQPSection {

	private Map<AMQPSymbol, Object> annotations;

	@Override
	public TLVAmqp getValue() {

		TLVMap map = new TLVMap();

		if (annotations != null)
			map = AMQPWrapper.wrapMap(annotations);

		DescribedConstructor constructor = new DescribedConstructor(map.getCode(), new TLVFixed(
				AMQPType.SMALL_ULONG, new byte[] { 0x78 }));
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
		return SectionCodes.FOOTER;
	}

	public Map<AMQPSymbol, Object> getAnnotations() {
		return annotations;
	}

	public void addAnnotation(String key, Object value) {
		if (annotations == null)
			annotations = new LinkedHashMap<AMQPSymbol, Object>();
		annotations.put(new AMQPSymbol(key), value);
	}

}
