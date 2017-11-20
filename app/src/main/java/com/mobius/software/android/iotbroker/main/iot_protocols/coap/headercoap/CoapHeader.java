package com.mobius.software.android.iotbroker.main.iot_protocols.coap.headercoap;

import android.util.Log;

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.CountableMessage;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Device;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Protocols;
import java.util.ArrayList;
import java.util.HashMap;
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

public class CoapHeader extends CountableMessage {

	private byte version;
	private CoapType type;
	private boolean isTokenExist;
	private int token;
	private CoapCode code;
	private int messageID;
	private String payload;
	private Map<CoapOptionType, List<String>> options;

	public CoapHeader() {
		this.version = 1;
		this.options = new HashMap<CoapOptionType, List<String>>();
	}

	public CoapHeader(CoapCode method, boolean isCon, boolean isToken, String payload) {
		this();
		this.code = method;
		this.type = isCon ? CoapType.CONFIRMABLE : CoapType.NON_CONFIRMABLE;
		this.isTokenExist = isToken;
		this.payload = payload;
	}

	public void addOption(CoapOptionType option, String value) {
		List<String> list = this.options.get(option);

		if (list == null) {
			list = new ArrayList<String>();
		}

		list.add(value);
		this.options.put(option, list);
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

	public byte getVersion() {
		return version;
	}

	public CoapType getCoapType() {
		return type;
	}

	public void setCoapType(CoapType type) {
		this.type = type;
	}

	public boolean isTokenExist() {
		return isTokenExist;
	}

	public void setTokenExist(boolean tokenExist) {
		isTokenExist = tokenExist;
	}

	public int getToken() {
		return token;
	}

	public void setToken(int token) {
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

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public Map<CoapOptionType, List<String>> getOptions() {
		return options;
	}

	public void setPacketID(Integer packetID) {
		this.messageID = packetID;
		this.token = packetID;
		this.packetID = packetID;
	}

}
