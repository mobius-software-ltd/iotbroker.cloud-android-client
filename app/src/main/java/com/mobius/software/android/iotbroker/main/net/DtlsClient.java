package com.mobius.software.android.iotbroker.main.net;

import android.util.Log;

import com.mobius.software.android.iotbroker.main.iot_protocols.classes.AbstractParser;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Encoder;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Handler;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.Message;
import com.mobius.software.android.iotbroker.main.iot_protocols.classes.UDPDecoder;
import com.mobius.software.android.iotbroker.main.listeners.ConnectionListener;

import com.mobius.software.iot.dal.crypto.AsyncDtlsClient;
import com.mobius.software.iot.dal.crypto.AsyncDtlsClientHandler;
import com.mobius.software.iot.dal.crypto.AsyncDtlsClientProtocol;
import com.mobius.software.iot.dal.crypto.DtlsStateHandler;

import org.bouncycastle.crypto.tls.ProtocolVersion;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.SecureRandom;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class DtlsClient implements InternetProtocol, DtlsStateHandler {

    private AsyncDtlsClientProtocol protocol=null;
    private SecureRandom SECURE_RANDOM = new SecureRandom();

    private InetSocketAddress address;
    private Integer workerThreads;

    private Bootstrap bootstrap;
    private NioEventLoopGroup loopGroup;
    private Channel datagramChannel;

    private Boolean isSecure = false;
    private KeyStore keyStore = null;
    private String keyStorePassword = "";

    private AbstractParser parser;

    public DtlsClient(InetSocketAddress address, Integer workerThreads) {
        this.address = address;
        this.workerThreads = workerThreads;
    }

    @Override
    public boolean init(final ConnectionListener listener, final AbstractParser parser) {

        if (datagramChannel == null) {
            this.parser = parser;

            loopGroup = new NioEventLoopGroup(workerThreads);

            bootstrap = new Bootstrap();
            bootstrap.group(loopGroup);
            bootstrap.channel(NioDatagramChannel.class);
            bootstrap.option(ChannelOption.SO_SNDBUF, 262144);

            final DtlsClient client = this;

            bootstrap.handler(new ChannelInitializer<DatagramChannel>() {
                @Override
                protected void initChannel(DatagramChannel datagramChannel) throws Exception {
                    AsyncDtlsClient asyncDtlsClient = new AsyncDtlsClient(keyStore, keyStorePassword, null);
                    protocol = new AsyncDtlsClientProtocol(asyncDtlsClient, SECURE_RANDOM, datagramChannel, null, client, address, true, ProtocolVersion.DTLSv12);
                    datagramChannel.pipeline().addLast(new AsyncDtlsClientHandler(protocol, client));

                    datagramChannel.pipeline().addLast(new UDPDecoder(parser));
                    datagramChannel.pipeline().addLast("handler", new Handler(listener));
                    datagramChannel.pipeline().addLast(new Encoder(parser));
                    datagramChannel.pipeline().addLast(new ExceptionHandler(listener));

                    //datagramChannel.pipeline().addLast(new DummyMessageHandler(client));
                }
            });

            bootstrap.remoteAddress(address);

            ChannelFuture future = bootstrap.bind(0).awaitUninterruptibly();
            datagramChannel = future.channel();
            datagramChannel.connect(address).awaitUninterruptibly();

            try
            {
                protocol.initHandshake(null);
            }
            catch(IOException ex)
            {
                Log.i("TAG", "An error occured while initializing handshake" + ex.getLocalizedMessage());
                return false;
            }

        }
        return true;
    }

    @Override
    public void send(Message message) {
        try {
            ByteBuf buffer = this.parser.encode(message);
            protocol.sendPacket(buffer);
        } catch (IOException e) {
            e.printStackTrace();
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

    @Override
    public void handshakeStarted(InetSocketAddress inetSocketAddress, Channel channel) {

    }

    @Override
    public void handshakeCompleted(InetSocketAddress inetSocketAddress, Channel channel) {

    }

    @Override
    public void errorOccured(InetSocketAddress inetSocketAddress, Channel channel) {

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

}
