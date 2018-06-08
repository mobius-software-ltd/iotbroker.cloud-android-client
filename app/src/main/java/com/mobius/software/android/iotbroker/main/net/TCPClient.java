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
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Encoder;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Handler;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.TCPDecoder;
import com.mobius.software.android.iotbroker.main.listeners.ConnectionListener;

import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;

public class TCPClient implements InternetProtocol {

	private InetSocketAddress address;
	private Integer workerThreads;

	private Bootstrap bootstrap;
	private NioEventLoopGroup loopGroup;
	private Channel channel;
	private ChannelFuture channelConnect;

	private Boolean isSecure = false;
	private KeyStore keyStore = null;
	private String keyStorePassword = "";

	public TCPClient(InetSocketAddress address, Integer workerThreads) {
		this.address = address;
		this.workerThreads = workerThreads;
	}

	public void setSecure(Boolean secure) {
		isSecure = secure;
	}

	public void setKeyStore(KeyStore keyStore) {
		this.keyStore = keyStore;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}

	@Override
	public boolean init(final ConnectionListener listener, final AbstractParser parser) {
		if (channel == null) {

			final SslContext sslContext = getSslContext();

			bootstrap = new Bootstrap();
			loopGroup = new NioEventLoopGroup(workerThreads);
			bootstrap.group(loopGroup);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.TCP_NODELAY, true);
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);

			bootstrap.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel socketChannel) throws Exception {
					if (isSecure) {
						socketChannel.pipeline().addLast("ssl", sslContext.newHandler(socketChannel.alloc()));
					}
					socketChannel.pipeline().addLast(new TCPDecoder(parser));
					socketChannel.pipeline().addLast("handler", new Handler(listener));
					socketChannel.pipeline().addLast(new Encoder(parser));
					socketChannel.pipeline().addLast(new ExceptionHandler(listener));
				}
			});
			bootstrap.remoteAddress(address);

			try {
				channelConnect = bootstrap.connect().sync();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
			catch (Exception ex) {
				ex.printStackTrace();
				return false;
			}
		}

		return true;
	}

	@Override
	public void send(Message message) {
		if (channel != null && channel.isOpen()) {
			channel.writeAndFlush(message);
		} else {
			Log.d("TAG", "Failed sending message " + message.getType());
		}
	}

	@Override
	public boolean isConnected() {
		if (channelConnect != null && channelConnect.isDone()) {
			channel = channelConnect.channel();
			return true;
		}
		return false;
	}

	@Override
	public void shutdown() {
		if (channel != null) {
			channel.close();
			channel = null;
		}

		if (loopGroup != null)
			loopGroup.shutdownGracefully();
	}

	@Override
	public void close() {
		if (channel != null) {
			channel.close();
			channel = null;
		}
		if (loopGroup != null) {
			loopGroup.shutdownGracefully();
			try {
				loopGroup.awaitTermination(1000, TimeUnit.MILLISECONDS);
			}
			catch (InterruptedException e) {

			}
		}
	}

	private SslContext getSslContext() {

		SslContext sslContext = null;

		try {
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			if (this.keyStore != null) {
				kmf.init(this.keyStore, this.keyStorePassword.toCharArray());
			} else {
				kmf.init(null, null);
			}
			SslContextBuilder sslContextBuilder = SslContextBuilder.forClient().keyManager(kmf);
			sslContext = sslContextBuilder.build();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return sslContext;
	}

}