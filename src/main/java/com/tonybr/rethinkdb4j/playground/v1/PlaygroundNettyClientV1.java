package com.tonybr.rethinkdb4j.playground.v1;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

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

    public static void handshake(String host, int port) throws Exception {
        try (RdbConnectionManager rdbConnectionManager = new RdbConnectionManager()) {
            try (RdbConnection rdbConnection = rdbConnectionManager.connectSync(host, port)) {
                rdbConnection.handshakeSync(null);
                //
                JsonArray query = new JsonArray();
                query.add(1);
                query.add("foo");
                query.add(new JsonObject());
                log.info("response for foo query -> {}", rdbConnection.executeSync(query));
            }
        }
    }

}
