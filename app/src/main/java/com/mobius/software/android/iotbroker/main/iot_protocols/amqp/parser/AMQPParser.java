package com.mobius.software.android.iotbroker.main.iot_protocols.amqp.parser;

import android.util.Log;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.HeaderCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.exceptions.MalformedHeaderException;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headeramqp.AMQPTransfer;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPFactory;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPPing;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.headerapi.AMQPProtoHeader;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.sections.AMQPSection;
import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.tlv.compound.TLVList;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.AbstractParser;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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

public class AMQPParser extends AbstractParser {

	private int getNext(ByteBuf buf) {
		buf.markReaderIndex();
		int length = buf.readInt();
		if (length == 1095586128) {
			int protocolId = buf.readByte();
			int versionMajor = buf.readByte();
			int versionMinor = buf.readByte();
			int versionRevision = buf.readByte();
			if ((protocolId == 0 || protocolId == 3) && versionMajor == 1 && versionMinor == 0
					&& versionRevision == 0) {
				buf.resetReaderIndex();
				return 8;
			}
		}
		buf.resetReaderIndex();
		return length;
	}

	public ByteBuf next(ByteBuf buf) {
		int size = getNext(buf);
		return Unpooled.buffer(size);
	}

	public Message decode(ByteBuf buf) {

		long length = buf.readInt() & 0xffffffffL;
		int doff = buf.readByte() & 0xff;
		int type = buf.readByte() & 0xff;
		int channel = buf.readShort() & 0xffff;

		// TODO check condition
		if (length == 8 && doff == 2 && (type == 0 || type == 1) && channel == 0)
			if (buf.readableBytes() == 0)
				return new AMQPPing();
			else
				throw new MalformedHeaderException("Received malformed ping-header with invalid length");

		// PTOROCOL-HEADER
		if (length == 1095586128 && (doff == 3 || doff == 0) && type == 1 && channel == 0)
			if (buf.readableBytes() == 0)
				return new AMQPProtoHeader(doff);
			else
				throw new MalformedHeaderException("Received malformed protocol-header with invalid length");

		if (length != buf.readableBytes() + 8)
			throw new MalformedHeaderException("Received malformed header with invalid length");

		AMQPHeader header = null;
		if (type == 0)
			header = AMQPFactory.getAMQP(buf);
		else if (type == 1)
			header = AMQPFactory.getSASL(buf);
		else
			throw new MalformedHeaderException("Received malformed header with invalid type: " + type);

		header.setDoff(doff);
		header.setHeaderType(type);
		header.setChannel(channel);

		if (header.getCode().equals(HeaderCodes.TRANSFER))
			while (buf.readableBytes() > 0)
				((AMQPTransfer) header).addSections(AMQPFactory.getSection(buf));

		return header;
	}

	public ByteBuf encode(Message message) {

		AMQPHeader header = (AMQPHeader)message;

		ByteBuf buf = null;

		if (header instanceof AMQPProtoHeader) {
			buf = Unpooled.buffer(8);
			buf.writeBytes("AMQP".getBytes(Charset.forName("US-ASCII")));
			buf.writeByte(((AMQPProtoHeader) header).getProtocolId());
			buf.writeByte(((AMQPProtoHeader) header).getVersionMajor());
			buf.writeByte(((AMQPProtoHeader) header).getVersionMinor());
			buf.writeByte(((AMQPProtoHeader) header).getVersionRevision());
			return buf;
		}

		if (header instanceof AMQPPing) {
			buf = Unpooled.buffer(8);
			buf.writeInt(8);
			buf.writeByte(header.getDoff());
			buf.writeByte(header.getHeaderType());
			buf.writeShort(header.getChannel());
			return buf;
		}

		int length = 8;

		TLVList arguments = header.getArguments();
		length += arguments.getLength();

		Set<AMQPSection> sections = null;
		if (header.getCode().equals(HeaderCodes.TRANSFER)) {
			sections = new LinkedHashSet<AMQPSection>(((AMQPTransfer) header).getSections().values());
			for (AMQPSection section : sections)
				length += section.getValue().getLength();
		}

		buf = Unpooled.buffer(length);

		buf.writeInt(length);

		int doff = header.getDoff();
		buf.writeByte(doff);

		int type = header.getHeaderType();
		buf.writeByte(type);

		int channel = header.getChannel();
		buf.writeShort(channel);

		buf.writeBytes(arguments.getBytes());

		if (sections != null)
			for (AMQPSection section : sections)
				buf.writeBytes(section.getValue().getBytes());

		return buf;
	}
}
