package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.HeaderCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;

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

public abstract class AMQPHeader implements Message {

	protected HeaderCodes code;

	protected int doff = 2;
	protected int headerType = 0;
	protected int channel = 0;

	public AMQPHeader(HeaderCodes code) {
		this.code = code;
	}

	public int getDoff() {
		return doff;
	}

	public void setDoff(int doff) {
		this.doff = doff;
	}

	public int getHeaderType() {
		return headerType;
	}

	public void setHeaderType(int type) {
		this.headerType = type;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public HeaderCodes getCode() {
		return code;
	}

	public abstract TLVList getArguments();

	public abstract void fillArguments(TLVList list);

	//public int getLength() { return 8; }

	public Protocols getProtocol() { return Protocols.AMQP_PROTOCOL; }

}
