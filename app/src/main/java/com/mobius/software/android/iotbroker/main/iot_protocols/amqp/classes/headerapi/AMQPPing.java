package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.HeaderCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;

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

public class AMQPPing extends AMQPHeader {

	public AMQPPing() {
		super(HeaderCodes.PING);
	}

	@Override
	public TLVList getArguments() {
		return null;
	}

	@Override
	public void fillArguments(TLVList list) {
	}

	@Override
	public int getLength() {
		int length = 8;
		return length;
	}

	@Override
	public int getType() {
		return HeaderCodes.PING.getType();
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}
}
