package com.tonybr.rethinkdb4j.playground.v1;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import java.nio.charset.StandardCharsets;

public class RdbChannelInitializer extends ChannelInitializer<SocketChannel> {
    
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addFirst(new RdbHandshakeHandler());
        ch.pipeline().addFirst(new RdbHandshakeEncoder());
        ch.pipeline().addFirst(new StringDecoder(StandardCharsets.US_ASCII));
        ch.pipeline().addFirst(new DelimiterBasedFrameDecoder(4096, true, Unpooled.buffer(1).writeByte(0)));
    }

}
