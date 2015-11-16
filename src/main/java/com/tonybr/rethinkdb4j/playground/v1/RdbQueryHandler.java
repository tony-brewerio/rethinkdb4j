package com.tonybr.rethinkdb4j.playground.v1;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RdbQueryHandler extends ChannelDuplexHandler {

    private ConcurrentMap<Long, RdbQuery> queries = new ConcurrentHashMap<>();

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof RdbQuery) {
            RdbQuery query = (RdbQuery) msg;
            queries.put(query.seq, query);
        }
        super.write(ctx, msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            ByteBuf buf = (ByteBuf) msg;
            long seq = buf.order(ByteOrder.BIG_ENDIAN).readLong();
            buf.skipBytes(4);
            String jsonString = buf.toString(StandardCharsets.UTF_8);
            JsonObject response = new Gson().fromJson(jsonString, JsonObject.class);
            RdbQuery query = queries.remove(seq);
            query.responsePromise.trySuccess(response);
        } else {
            super.channelRead(ctx, msg);
        }
    }

}
