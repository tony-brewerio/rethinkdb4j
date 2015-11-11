package com.tonybr.rethinkdb4j.playground.v1;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class RdbHandshakeEncoder extends MessageToByteEncoder<RdbHandshake> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RdbHandshake msg, ByteBuf out) throws Exception {
        ByteBuf authKeyBuffer = Unpooled.copiedBuffer(
                msg.authKey != null ? msg.authKey : "",
                StandardCharsets.UTF_8
        );
        out.order(ByteOrder.LITTLE_ENDIAN)
                .writeInt(msg.version.val)
                .writeInt(authKeyBuffer.readableBytes())
                .writeBytes(authKeyBuffer)
                .writeInt(msg.protocol.val);
    }

}
