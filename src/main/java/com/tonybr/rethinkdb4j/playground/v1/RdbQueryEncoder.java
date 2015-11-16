package com.tonybr.rethinkdb4j.playground.v1;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class RdbQueryEncoder extends MessageToByteEncoder<RdbQuery> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RdbQuery msg, ByteBuf out) throws Exception {
        Gson gson = new Gson();
        String jsonString = gson.toJson(msg.query);
        ByteBuf jsonBuffer = Unpooled.copiedBuffer(jsonString, StandardCharsets.UTF_8);
        out.order(ByteOrder.BIG_ENDIAN).writeLong(msg.seq)
                .order(ByteOrder.LITTLE_ENDIAN).writeInt(jsonBuffer.readableBytes())
                .writeBytes(jsonBuffer);
    }

}
