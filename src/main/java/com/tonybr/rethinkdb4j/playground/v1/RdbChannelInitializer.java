package com.tonybr.rethinkdb4j.playground.v1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.StandardCharsets;

public class RdbChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final ByteBuf ZERO_BYTE = Unpooled.copiedBuffer(new byte[]{0});

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("handshakeFrameDecoder", new DelimiterBasedFrameDecoder(4096, true, ZERO_BYTE));
        ch.pipeline().addLast("handshakeStringDecoder", new StringDecoder(StandardCharsets.US_ASCII));
        ch.pipeline().addLast("handshakeEncoder", new RdbHandshakeEncoder());
        ch.pipeline().addLast("handshakeResponseHandler", new RdbHandshakeHandler());
    }

}
