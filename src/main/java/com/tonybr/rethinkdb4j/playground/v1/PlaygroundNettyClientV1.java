package com.tonybr.rethinkdb4j.playground.v1;

import com.tonybr.rethinkdb4j.ql2.Protocol;
import com.tonybr.rethinkdb4j.ql2.Version;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Promise;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaygroundNettyClientV1 {

    private static final Logger log = LoggerFactory.getLogger(PlaygroundNettyClientV1.class);

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream(new File("gradle.properties"))) {
            properties.load(is);
        }
        String host = properties.getProperty("docker.rethinkdb.host");
        int port = Integer.parseInt(properties.getProperty("docker.rethinkdb.port"));
        log.info("using host: {}, port: {}", host, port);
        handshake(host, port);
    }
    
    public static String handshake(String host, int port) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new RdbChannelInitializer());
            //
            ChannelFuture channelFuture = b.connect(host, port);
            channelFuture.get(10, TimeUnit.SECONDS);
            Channel channel = channelFuture.channel();
            log.info("connected to server");
            try {
                Promise<String> promise = channel.eventLoop().newPromise();
                channel.writeAndFlush(new RdbHandshake(promise, Version.V0_4, Protocol.JSON));
                log.info("handshake sent to server");
                String response = promise.get(10, TimeUnit.SECONDS);
                log.info("handshake reply: {}", response);
                return response;
            } finally {
                channel.close().await(10, TimeUnit.SECONDS);
            }
        } finally {
            workerGroup.shutdownGracefully().await(10, TimeUnit.SECONDS);
        }
    }

}
