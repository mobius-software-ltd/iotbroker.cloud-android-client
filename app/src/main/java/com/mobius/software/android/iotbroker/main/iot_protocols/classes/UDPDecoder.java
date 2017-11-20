package com.mobius.software.android.iotbroker.main.iot_protocols.classes;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

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

public class UDPDecoder extends MessageToMessageDecoder {

    private AbstractParser parser;

    public UDPDecoder(AbstractParser parser)
    {
        this.parser = parser;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Object o, List list) throws Exception
    {
        if (o instanceof DatagramPacket) {
            DatagramPacket packet = (DatagramPacket)o;
            ByteBuf buf = packet.content();
            if (buf.readableBytes() > 1) {
                try {
                    Message header = parser.decode(buf);
                    list.add(header);
                } catch (Exception e) {
                    buf.resetReaderIndex();
                    channelHandlerContext.channel().pipeline().remove(this);
                    throw e;
                }
            }
        }
    }
}
