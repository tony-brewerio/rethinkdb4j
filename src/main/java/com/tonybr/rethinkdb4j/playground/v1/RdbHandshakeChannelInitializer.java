package com.tonybr.rethinkdb4j.playground.v1;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.StandardCharsets;

public class RdbHandshakeChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("handshakeFrameDecoder", new DelimiterBasedFrameDecoder(4096, true, Delimiters.nulDelimiter()));
        ch.pipeline().addLast("handshakeStringDecoder", new StringDecoder(StandardCharsets.US_ASCII));
        ch.pipeline().addLast("handshakeEncoder", new RdbHandshakeEncoder());
        ch.pipeline().addLast("handshakeResponseHandler", new RdbHandshakeHandler());
    }

}
