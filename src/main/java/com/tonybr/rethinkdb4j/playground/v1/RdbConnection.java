package com.tonybr.rethinkdb4j.playground.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class RdbConnection implements AutoCloseable {

    private final Channel channel;

    private AtomicBoolean handshakeSent = new AtomicBoolean();

    private AtomicLong querySequence = new AtomicLong();

    public RdbConnection(Channel channel) {
        this.channel = channel;
    }

    public Future<String> handshake(String authKey) {
        Promise<String> responsePromise = channel.eventLoop().newPromise();
        channel.closeFuture().addListener(closeFuture -> {
            if (closeFuture.isSuccess()) {
                responsePromise.tryFailure(new ChannelException("handshake on closed channel"));
            } else {
                responsePromise.tryFailure(closeFuture.cause());
            }
        });
        channel.eventLoop().schedule(
                () -> responsePromise.tryFailure(new TimeoutException("handshake response timed out")),
                1, TimeUnit.SECONDS
        );
        if (handshakeSent.compareAndSet(false, true)) {
            channel.writeAndFlush(new RdbHandshake(authKey, responsePromise));
        } else {
            responsePromise.tryFailure(new IllegalStateException("handshake already sent"));
        }
        return responsePromise;
    }

    public String handshakeSync(String authKey) {
        Future<String> responseFuture = handshake(authKey);
        responseFuture.syncUninterruptibly();
        return responseFuture.getNow();
    }

    public Future<JsonObject> execute(JsonElement query) {
        Promise<JsonObject> responsePromise = channel.eventLoop().newPromise();
        channel.closeFuture().addListener(closeFuture -> {
            if (closeFuture.isSuccess()) {
                responsePromise.tryFailure(new ChannelException("query on closed channel"));
            } else {
                responsePromise.tryFailure(closeFuture.cause());
            }
        });
        channel.eventLoop().schedule(
                () -> responsePromise.tryFailure(new TimeoutException("query response timed out")),
                1, TimeUnit.SECONDS
        );
        channel.writeAndFlush(new RdbQuery(querySequence.incrementAndGet(), query, responsePromise));
        return responsePromise;
    }

    public JsonObject executeSync(JsonElement query) {
        Future<JsonObject> responseFuture = execute(query);
        responseFuture.awaitUninterruptibly();
        return responseFuture.getNow();
    }

    @Override
    public void close() throws Exception {
        channel.close();
    }

}
