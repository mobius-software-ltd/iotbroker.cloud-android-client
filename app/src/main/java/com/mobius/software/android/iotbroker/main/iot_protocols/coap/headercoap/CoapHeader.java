package com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap;

import android.util.Log;

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.CountableMessage;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import com.mobius.software.android.iotbroker.main.utility.ConvertorUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

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

public class CoapHeader extends CountableMessage {

	private Integer version;
	private CoapType type;
	private byte[] token;
	private CoapCode code;
	private int messageID;
	private byte[] payload;
	private List<CoapOption> options;

    @Override
    public String toString() {
        return "CoapMessage [version=" + version + ", type=" + type + ", code=" + code + ", messageID=" + messageID + ", token=" + Arrays.toString(token) + ", options=" + options + ", payload=" + Arrays.toString(payload) + "]";
    }

    private CoapHeader(Integer version, CoapType type, CoapCode code, Integer messageID, byte[] token, List<CoapOption> options, byte[] payload) {
        this.version = version;
        this.type = type;
        this.code = code;
        this.messageID = messageID;
        this.token = token;
        this.options = options;
        this.payload = payload;
    }

	public CoapHeader() {
		this.version = 1;
		this.options = new ArrayList<>();
	}

	public CoapHeader(CoapCode method, boolean isCon, String payload) {
		this();
		this.code = method;
		this.type = isCon ? CoapType.CONFIRMABLE : CoapType.NON_CONFIRMABLE;
		this.payload = payload.getBytes();
	}

	public void addOption(CoapOptionType option, String value) {
		this.options.add(new CoapOption(option.getType(), value.length(), value.getBytes()));
	}

    public void addOption(CoapOption option) {
        this.options.add(option);
    }

	public String getOptionValue(CoapOptionType option) {
        for (int i = 0; i < this.options.size(); i++) {
            CoapOption item = this.options.get(i);
            if (item.getNumber() == option.getType()) {
                return new String(item.getValue());
            }
        }
        return null;
    }

	public CoapOption getOption(CoapOptionType option) {
		for (int i = 0; i < this.options.size(); i++) {
			CoapOption item = this.options.get(i);
			if (item.getNumber() == option.getType()) {
				return item;
			}
		}
		return null;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public int getType() {
		return this.type.getType();
	}

	@Override
	public Protocols getProtocol() {
		return Protocols.COAP_PROTOCOL;
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}

	public Integer getVersion() {
		return version;
	}

    public void setVersion(Integer version) {
        this.version = version;
    }

    public CoapType getCoapType() {
		return type;
	}

	public void setCoapType(CoapType type) {
		this.type = type;
	}

	public byte[] getToken() {
		return token;
	}

	public void setToken(byte[] token) {
		this.token = token;
	}

	public CoapCode getCode() {
		return code;
	}

	public void setCode(CoapCode code) {
		this.code = code;
	}

	public int getMessageID() {
		return messageID;
	}

	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

    public List<CoapOption> getOptions() {
        List<CoapOption> copy = this.options;
        Collections.sort(copy, new CoapOptionsComparator());
        return copy;
    }

    public void setPacketID(Integer packetID) {
		this.messageID = packetID;
		this.token = String.valueOf(packetID).getBytes();
		this.packetID = packetID;
	}

}