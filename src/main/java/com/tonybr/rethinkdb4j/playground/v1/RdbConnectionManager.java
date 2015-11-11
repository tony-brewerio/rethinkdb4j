package com.tonybr.rethinkdb4j.playground.v1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class RdbConnectionManager implements AutoCloseable {

    private final EventLoopGroup eventLoopGroup;
    private final Bootstrap bootstrap;

    public RdbConnectionManager() {
        this.eventLoopGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .handler(new RdbChannelInitializer());
    }

    public Future<RdbConnection> connect(String host, int port) {
        Promise<RdbConnection> connectionPromise = eventLoopGroup.next().newPromise();
        bootstrap.connect(host, port).addListener((ChannelFuture future) -> {
            if (future.isSuccess()) {
                connectionPromise.setSuccess(new RdbConnection(future.channel()));
            } else {
                connectionPromise.setFailure(future.cause());
            }
        });
        return connectionPromise;
    }

    public RdbConnection connectSync(String host, int port) {
        Future<RdbConnection> connectionPromise = connect(host, port);
        connectionPromise.awaitUninterruptibly();
        return connectionPromise.getNow();
    }

    @Override
    public void close() throws Exception {
        eventLoopGroup.shutdownGracefully().awaitUninterruptibly();
    }

}
