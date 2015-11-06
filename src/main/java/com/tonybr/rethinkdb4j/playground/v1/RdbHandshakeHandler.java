package com.tonybr.rethinkdb4j.playground.v1;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipelineException;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RdbHandshakeHandler extends ChannelDuplexHandler {
    
    private static final Logger log = LoggerFactory.getLogger(RdbHandshakeHandler.class);

    private RdbHandshake handshake;

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof RdbHandshake) {
            if (handshake == null) {
                handshake = (RdbHandshake) msg;
                log.info("sending handshake {} - {}", handshake.version, handshake.protocol);
            } else {
                throw new ChannelPipelineException("duplicate handshake");
            }
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String) {
            if (handshake != null) {
                if (handshake.promise.isDone()) {
                    throw new ChannelPipelineException("handshake response was received already");
                } else {
                    handshake.promise.setSuccess((String) msg);
                }
            } else {
                throw new ChannelPipelineException("got handshake response when there was no handshake");
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }
    
}
