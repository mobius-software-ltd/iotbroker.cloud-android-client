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

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.net.SocketAddress;

import android.util.Log;

import com.mobius.software.android.iotbroker.main.listeners.ConnectionListener;

@Sharable
public class ExceptionHandler extends ChannelDuplexHandler {
	private static final String separator = ",";

	private ConnectionListener listener;
	public ExceptionHandler(ConnectionListener listener)
	{
		this.listener=listener;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		SocketAddress address = ctx.channel().remoteAddress();
		if (ctx.channel().isOpen())
			ctx.channel().close();

		StringBuilder sb = new StringBuilder();
		sb.append(address).append(separator);
		sb.append(
				cause.getClass()
						.getName()
						.substring(
								cause.getClass().getName().lastIndexOf(".") + 1))
				.append(separator);
		sb.append(cause.getMessage().substring(
				cause.getMessage().lastIndexOf(".") + 1));
		Log.d("", sb.toString());
	}

	@Override
	public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress,
			SocketAddress localAddress, ChannelPromise promise) {
		ctx.connect(remoteAddress, localAddress,
				promise.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(ChannelFuture future) {
						if (!future.isSuccess())
							Log.i("ERROR", "an error occured while connect");
					}
				}));
	}

	@Override
	public void write(ChannelHandlerContext ctx, final Object msg, ChannelPromise promise) {
		ctx.write(msg, promise.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) {
				if (!future.isSuccess())
				{
					Log.i("ERROR", "an error occured while write");
					if(listener!=null)
						listener.writeError();
				}
			}
		}));
	}
}
