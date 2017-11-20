package com.mobius.software.android.iotbroker.main.net;

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

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.AbstractParser;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.TCPDecoder;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Encoder;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Handler;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.UDPDecoder;
import com.mobius.software.android.iotbroker.main.iot_protocols.mqtt_sn.parser.avps.SNType;
import com.mobius.software.android.iotbroker.main.listeners.ConnectionListener;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import java.net.InetSocketAddress;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class UDPClient implements InternetProtocol {

    private InetSocketAddress address;
    private Integer workerThreads;

    private Bootstrap bootstrap;
    private NioEventLoopGroup loopGroup;
    private Channel datagramChannel;

    public UDPClient(InetSocketAddress address, Integer workerThreads) {
        this.address = address;
        this.workerThreads = workerThreads;
    }

    @Override
    public boolean init(final ConnectionListener listener, final AbstractParser parser) {

        if (datagramChannel == null) {
            bootstrap = new Bootstrap();
            bootstrap.channel(NioDatagramChannel.class);
            loopGroup = new NioEventLoopGroup(workerThreads);
            bootstrap.group(loopGroup);
            bootstrap.option(ChannelOption.SO_SNDBUF, 262144);

            bootstrap.handler(new ChannelInitializer<DatagramChannel>() {
                @Override
                protected void initChannel(DatagramChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new UDPDecoder(parser));
                    socketChannel.pipeline().addLast("handler", new Handler(listener));
                    socketChannel.pipeline().addLast(new Encoder(parser));
                    socketChannel.pipeline().addLast(new ExceptionHandler(listener));
                }
            });

            ChannelFuture future = bootstrap.bind(0).awaitUninterruptibly();
            datagramChannel = future.channel();
            datagramChannel.connect(address).awaitUninterruptibly();
        }
        return true;
    }

    @Override
    public void send(Message message) {
        Log.v("TAG", " - try send: "+ SNType.valueOf(message.getType()).toString());
        if (datagramChannel != null && datagramChannel.isOpen()) {
            Log.v("TAG", " - send: "+ SNType.valueOf(message.getType()).toString());
            datagramChannel.writeAndFlush(message);
        } else {
            Log.d("TAG", "Failed sending message " + message.getType());
        }
    }

    @Override
    public boolean isConnected() {
        if (datagramChannel != null) {
            return true;
        }
        return false;
    }

    @Override
    public void shutdown() {
        if (datagramChannel != null) {
            datagramChannel.close();
            datagramChannel = null;
        }

        if (loopGroup != null)
            loopGroup.shutdownGracefully();
    }

    @Override
    public void close() {
        this.shutdown();
    }
}
