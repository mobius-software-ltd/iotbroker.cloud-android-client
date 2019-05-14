package com.mobius.software.android.iotbroker.main.net;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.AbstractParser;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.MQJsonParser;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.listeners.ConnectionListener;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;

public class WebsocketClient implements InternetProtocol {

    private InetSocketAddress address;
    private Integer workerThreads;

    private Bootstrap bootstrap;
    private NioEventLoopGroup loopGroup;
    private Channel channel;
    private ChannelFuture channelConnect;

    private Boolean isSecure = false;
    private KeyStore keyStore = null;
    private String keyStorePassword = "";

    private Channel ch;

    public WebsocketClient(InetSocketAddress address, Integer workerThreads) {
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

    public boolean init(final ConnectionListener listener, final AbstractParser parser) {

        final SslContext sslContext = getSslContext();

        this.bootstrap = new Bootstrap();
        this.loopGroup = new NioEventLoopGroup(this.workerThreads);
        this.bootstrap.group(this.loopGroup);
        this.bootstrap.channel(NioSocketChannel.class);

        WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                this.getUri(), WebSocketVersion.V13, null, false,
                EmptyHttpHeaders.INSTANCE, 1280000);

        final WebSocketClientHandler handler = new WebSocketClientHandler(handshaker, listener);

        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel socketChannel) {
                if (isSecure) {
                    socketChannel.pipeline().addLast("ssl", sslContext.newHandler(socketChannel.alloc()));
                }
                socketChannel.pipeline().addLast("http-codec", new HttpClientCodec());
                socketChannel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                socketChannel.pipeline().addLast("ws-handler", handler);
            }
        });

        bootstrap.remoteAddress(address);

        try {
            channelConnect = bootstrap.connect().sync();
            return handler.handshakeFuture().await(3,TimeUnit.SECONDS);
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void send(Message message) {
        if (channel != null && channel.isOpen()) {
            String string = null;
            try {
                string = new MQJsonParser().jsonString(message);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            channel.writeAndFlush(new TextWebSocketFrame(string));
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
            channel.writeAndFlush(new CloseWebSocketFrame());
            try {
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            channel.close();
            channel = null;
        }

        if (loopGroup != null)
            loopGroup.shutdownGracefully();
    }

    @Override
    public void close() {
        if (channel != null) {
            channel.writeAndFlush(new CloseWebSocketFrame());
            try {
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    private URI getUri() {

        String type = this.isSecure ? "wss" : "ws";
        String url = type + "://" + this.address.getHostName() + ":" + String.valueOf(this.address.getPort()) + "/ws";
        URI uri;
        try {
            uri = new URI(url);
        } catch (Exception e) {
            return null;
        }
        return uri;
    }

}
