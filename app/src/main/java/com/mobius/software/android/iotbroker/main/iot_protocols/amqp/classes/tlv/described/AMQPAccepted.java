package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.described;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.DescribedConstructor;
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

public class AMQPAccepted implements AMQPOutcome, AMQPState {

	@Override
	public TLVList getList() {
		TLVList list = new TLVList();
		DescribedConstructor constructor = new DescribedConstructor(list.getCode(), new TLVFixed(
				AMQPType.SMALL_ULONG, new byte[] { 0x24 }));
		list.setConstructor(constructor);
		return list;
	}

	@Override
	public void fill(TLVList list) {
	}

}
