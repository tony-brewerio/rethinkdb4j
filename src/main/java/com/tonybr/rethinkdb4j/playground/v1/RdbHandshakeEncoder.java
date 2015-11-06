package com.tonybr.rethinkdb4j.playground.v1;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.nio.ByteOrder;

public class RdbHandshakeEncoder extends MessageToByteEncoder<RdbHandshake> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RdbHandshake msg, ByteBuf out) throws Exception {
        out.order(ByteOrder.LITTLE_ENDIAN).writeInt(msg.version.val).writeInt(0).writeInt(msg.protocol.val);
    }

}
