package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.AMQPType;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.constructor.SimpleConstructor;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.api.TLVAmqp;

import java.nio.ByteBuffer;
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

public class TLVMap extends TLVAmqp {

	int width, size, count;

	protected Map<TLVAmqp, TLVAmqp> map = new LinkedHashMap<TLVAmqp, TLVAmqp>();

	public TLVMap() {
		super(new SimpleConstructor(AMQPType.MAP_8));
		width = 1;
		size = 1;
		count = 0;
	}

	public TLVMap(AMQPType code, Map<TLVAmqp, TLVAmqp> map) {
		super(new SimpleConstructor(code));
		this.map = map;
		width = code.equals(AMQPType.MAP_8) ? 1 : 4;
		size += width;
		for (Map.Entry<TLVAmqp, TLVAmqp> entry : map.entrySet()) {
			size += entry.getKey().getLength();
			size += entry.getValue().getLength();
		}
		count = map.size();
	}

	public byte[] getValue() {
		return null;
	}

	public void putElement(TLVAmqp key, TLVAmqp value) {
		map.put(key, value);
		size += key.getLength() + value.getLength();
		count++;
		update();
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<TLVAmqp, TLVAmqp> entry : map.entrySet()) {
			sb.append(entry.getKey().toString());
			sb.append(" : ");
			sb.append(entry.getValue().toString());
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public byte[] getBytes() {
		byte[] constructorBytes = constructor.getBytes();
		byte[] sizeBytes = new byte[width];
		switch (width) {
		case 1:
			ByteBuffer.wrap(sizeBytes).put((byte) size);
			break;
		default:
			ByteBuffer.wrap(sizeBytes).putInt(size);
			break;
		}

		byte[] countBytes = new byte[width];
		switch (width) {
		case 1:
			ByteBuffer.wrap(countBytes).put((byte) (count * 2));
			break;
		default:
			ByteBuffer.wrap(countBytes).putInt(count * 2);
			break;
		}

		byte[] valueBytes = new byte[size - width];
		int pos = 0;
		byte[] keyBytes;
		byte[] valBytes;
		for (Map.Entry<TLVAmqp, TLVAmqp> entry : map.entrySet()) {
			keyBytes = entry.getKey().getBytes();
			valBytes = entry.getValue().getBytes();
			System.arraycopy(keyBytes, 0, valueBytes, pos, keyBytes.length);
			pos += keyBytes.length;
			System.arraycopy(valBytes, 0, valueBytes, pos, valBytes.length);
			pos += valBytes.length;
		}

		byte[] bytes = new byte[constructorBytes.length + sizeBytes.length + countBytes.length
				+ valueBytes.length];
		System.arraycopy(constructorBytes, 0, bytes, 0, constructorBytes.length);
		if (size > 0) {
			System.arraycopy(sizeBytes, 0, bytes, constructorBytes.length, sizeBytes.length);
			System.arraycopy(countBytes, 0, bytes, constructorBytes.length + sizeBytes.length,
					countBytes.length);
			System.arraycopy(valueBytes, 0, bytes, constructorBytes.length + sizeBytes.length
					+ countBytes.length, valueBytes.length);
		}
		return bytes;
	}

	protected void update() {
		if (width == 1 && size > 255) {
			constructor.setCode(AMQPType.MAP_32);
			width = 4;
			size += 3;
		}
	}

	public Map<TLVAmqp, TLVAmqp> getMap() {
		return map;
	}

	public int getLength() {
		return constructor.getLength() + width + size;
	}

}
