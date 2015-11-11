package com.tonybr.rethinkdb4j.playground.v1;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Promise;

public class RdbHandshakeHandler extends ChannelDuplexHandler {

    private Promise<String> responsePromise;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof RdbHandshake) {
            this.responsePromise = ((RdbHandshake) msg).responsePromise;
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String) {
            responsePromise.trySuccess((String) msg);
        } else {
            super.channelRead(ctx, msg);
        }
    }

}
