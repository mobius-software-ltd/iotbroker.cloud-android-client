package com.mobius.software.android.iotbroker.main.iot_protocols.classes;

/**
 * Mobius Software LTD
 * Copyright 2015-2016, Mobius Software LTD
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

import android.util.Log;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.Arrays;
import java.util.List;

import com.mobius.software.android.iotbroker.main.iot_protocols.amqp.classes.codes.HeaderCodes;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt.parser.MQParser;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;

public class TCPDecoder extends ByteToMessageDecoder
{
	private AbstractParser parser;

	public TCPDecoder(AbstractParser parser)
	{
		this.parser = parser;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception
	{
		ByteBuf nextHeader = null;
		do {
			if (buf.readableBytes() > 1)
				nextHeader = parser.next(buf);

			if (nextHeader != null) {
				buf.readBytes(nextHeader, nextHeader.capacity());
				try {
					Message header = parser.decode(nextHeader);
					out.add(header);
				}
				catch (Exception e) {
					buf.resetReaderIndex();
					ctx.channel().pipeline().remove(this);
					throw e;
				}
				finally {
					nextHeader.release();
				}
			}
		} while (buf.readableBytes() > 1 && nextHeader != null);
	}

}
