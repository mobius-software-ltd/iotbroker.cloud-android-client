package com.mobius.software.android.iotbroker.mqtt.net;

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

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import android.util.Log;

import com.mobius.software.android.iotbroker.mqtt.listeners.ConnectionListener;
import com.mobius.software.android.iotbroker.mqtt.parser.header.api.MQMessage;

public class TCPClient {
	private InetSocketAddress address;
	private Integer workerThreads;

	private Bootstrap bootstrap;
	private NioEventLoopGroup loopGroup;
	private Channel channel;
	private ChannelFuture channelConnect;

	// handlers for client connections

	public TCPClient(InetSocketAddress address, Integer workerThreads) {
		this.address = address;
		this.workerThreads = workerThreads;
	}

	public void shutdown() {
		if (channel != null) {
			channel.close();
			channel = null;
		}
		
		if (loopGroup != null)
			loopGroup.shutdownGracefully();		
	}

	public void close() {
		if (channel != null) {
			channel.close();
			channel = null;
		}
		if (loopGroup != null) {
			loopGroup.shutdownGracefully();
			try {
				loopGroup.awaitTermination(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {

			}
		}
	}

	public boolean init(final ConnectionListener listener) {
		if (channel == null) {
			bootstrap = new Bootstrap();
			loopGroup = new NioEventLoopGroup(workerThreads);
			bootstrap.group(loopGroup);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.TCP_NODELAY, true);
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

			bootstrap.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel socketChannel)
						throws Exception {
					socketChannel.pipeline().addLast(new MQDecoder());
					socketChannel.pipeline().addLast("handler",
							new MQHandler(listener));
					socketChannel.pipeline().addLast(new MQEncoder());
					socketChannel.pipeline().addLast(new ExceptionHandler(listener));
				}
			});
			bootstrap.remoteAddress(address);
			try {
				channelConnect = bootstrap.connect().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}

		return true;
	}

	public void send(MQMessage message) {
		Log.d("SEND_MESSAGE", message.getType().toString());
		if (channel != null && channel.isOpen())
			channel.writeAndFlush(message);
		else {
			Log.d("TAG", "Failed sending message "
					+ message.getType().toString());
		}
	}

	public boolean isConnected() {
		if (channelConnect != null && channelConnect.isDone()) {
			channel = channelConnect.channel();
			return true;
		}

		return false;

	}
}