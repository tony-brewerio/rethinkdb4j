package com.tonybr.rethinkdb4j.playground.v1;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteOrder;

public class RdbQueryChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(
                ByteOrder.LITTLE_ENDIAN,
                Integer.MAX_VALUE,
                8, 4, 0, 0, true
        ));
        ch.pipeline().addLast("queryEncoder", new RdbQueryEncoder());
        ch.pipeline().addLast("queryHandler", new RdbQueryHandler());
    }

}
