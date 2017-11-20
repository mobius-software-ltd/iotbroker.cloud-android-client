package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.HeaderCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
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

public class AMQPProtoHeader extends AMQPHeader {

	private final String protocol = "AMQP";
	private final int protocolId;
	private final int versionMajor = 1;
	private final int versionMinor = 0;
	private final int versionRevision = 0;

	public AMQPProtoHeader(int protocolId) {
		super(null);
		if (protocolId != 0 && protocolId != 3)
			throw new IllegalArgumentException();
		this.protocolId = protocolId;
	}

	public String getProtocolString() {
		return protocol;
	}

	public int getProtocolId() {
		return protocolId;
	}

	public int getVersionMajor() {
		return versionMajor;
	}

	public int getVersionMinor() {
		return versionMinor;
	}

	public int getVersionRevision() {
		return versionRevision;
	}

	public byte[] getBytes() {
		byte[] bytes = new byte[8];
		System.arraycopy(protocol.getBytes(), 0, bytes, 0, protocol.length());
		bytes[4] = (byte) protocolId;
		bytes[5] = (byte) versionMajor;
		bytes[6] = (byte) versionMinor;
		bytes[7] = (byte) versionRevision;
		return bytes;
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
		return HeaderCodes.PROTO.getType();
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}
}
