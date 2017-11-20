package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headersasl;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.HeaderCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.DescribedConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.exceptions.MalformedHeaderException;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPUnwrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPWrapper;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.fixed.TLVFixed;
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

public class SASLResponse extends AMQPHeader {

	private byte[] response;

	public SASLResponse() {
		super(HeaderCodes.RESPONSE);
	}

	@Override
	public TLVList getArguments() {
		TLVList list = new TLVList();

		if (response == null)
			throw new MalformedHeaderException("SASL-Response header's challenge can't be null");
		list.addElement(0, AMQPWrapper.wrap(response));

		DescribedConstructor constructor = new DescribedConstructor(list.getCode(),
				new TLVFixed(AMQPType.SMALL_ULONG, new byte[] { 0x43 }));
		list.setConstructor(constructor);

		return list;
	}

	@Override
	public void fillArguments(TLVList list) {

		int size = list.getList().size();

		if (size == 0)
			throw new MalformedHeaderException("Received malformed SASL-Response header: challenge can't be null");

		if (size > 1)
			throw new MalformedHeaderException(
					"Received malformed SASL-Response header. Invalid number of arguments: " + size);

		if (size > 0) {
			TLVAmqp element = list.getList().get(0);
			if (element.isNull())
				throw new MalformedHeaderException("Received malformed SASL-Response header: challenge can't be null");
			response = AMQPUnwrapper.unwrapBinary(element);
		}
	}

	private byte[] calcCramMD5(byte[] challenge, String user) throws Exception {
		if (challenge != null && challenge.length != 0) {
			try {
				SecretKeySpec key = new SecretKeySpec(user.getBytes("ASCII"), "HMACMD5");
				Mac mac = Mac.getInstance("HMACMD5");
				mac.init(key);

				byte[] bytes = mac.doFinal(challenge);

				StringBuffer hash = new StringBuffer(user);
				hash.append(' ');
				for (int i = 0; i < bytes.length; i++) {
					String hex = Integer.toHexString(0xFF & bytes[i]);
					if (hex.length() == 1) {
						hash.append('0');
					}
					hash.append(hex);
				}
				return hash.toString().getBytes("ASCII");
			} catch (Exception e) {
				throw new Exception("calcCramMD5 error", e);
			}
		} else {
			return new byte[0];
		}
	}

	public byte[] getResponse() {
		return response;
	}

	public void setCramMD5Response(byte[] challenge, String user) throws Exception {
		if (user == null)
			throw new IllegalArgumentException(
					"CramMD5 response generator must be provided with a non-null username value");
		if (challenge == null)
			throw new IllegalArgumentException(
					"CramMD5 response generator must be provided with a non-null challenge value");
		this.response = calcCramMD5(challenge, user);
	}

	@Override
	public int getLength() {
		int length = 8;
		TLVAmqp arguments = this.getArguments();
		length += arguments.getLength();
		return length;
	}

	@Override
	public int getType() {
		return HeaderCodes.RESPONSE.getType();
	}

	@Override
	public void processBy(Device device) {
		device.processMessage(this);
	}
}
