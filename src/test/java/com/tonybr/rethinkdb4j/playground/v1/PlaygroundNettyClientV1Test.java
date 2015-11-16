package com.tonybr.rethinkdb4j.playground.v1;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.netty.channel.ChannelException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class PlaygroundNettyClientV1Test {

    public static RdbConnectionManager rdbConnectionManager;

    public String host;
    public int port;

    @BeforeClass
    public static void initConnectionManager() throws Exception {
        rdbConnectionManager = new RdbConnectionManager();
    }

    @AfterClass
    public static void closeConnectionManager() throws Exception {
        rdbConnectionManager.close();
    }

    @Before
    public void loadProperties() throws IOException {
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream(new File("gradle.properties"))) {
            properties.load(is);
        }
        host = properties.getProperty("docker.rethinkdb.host");
        port = Integer.parseInt(properties.getProperty("docker.rethinkdb.port"));
    }

    @org.junit.Test
    public void testEmptyHandshake() throws Exception {
        try (RdbConnection rdbConnection = rdbConnectionManager.connectSync(host, port)) {
            assertThat(rdbConnection.handshakeSync(null), equalTo("SUCCESS"));
        }
    }

    @org.junit.Test
    public void testInvalidHandshake() throws Exception {
        try (RdbConnection rdbConnection = rdbConnectionManager.connectSync(host, port)) {
            assertThat(rdbConnection.handshakeSync("badAuthKey"), equalTo("ERROR: Incorrect authorization key.\n"));
        }
    }

    @org.junit.Test
    public void testDoubleHandshake() throws Exception {
        try (RdbConnection rdbConnection = rdbConnectionManager.connectSync(host, port)) {
            assertThat(rdbConnection.handshakeSync(null), equalTo("SUCCESS"));
            try {
                rdbConnection.handshakeSync(null);
            } catch (IllegalStateException e) {
                assertThat(e.getMessage(), containsString("already"));
            }
        }
    }

    @org.junit.Test
    public void testClosedHandshake() throws Exception {
        try (RdbConnection rdbConnection = rdbConnectionManager.connectSync(host, port)) {
            rdbConnection.close();
            try {
                rdbConnection.handshakeSync(null);
            } catch (ChannelException e) {
                assertThat(e.getMessage(), containsString("closed"));
            }
        }
    }

    @org.junit.Test
    public void testSimplestPossibleQuery() throws Exception {
        try (RdbConnection rdbConnection = rdbConnectionManager.connectSync(host, port)) {
            rdbConnection.handshakeSync(null);
            JsonArray query = new JsonArray();
            query.add(1);
            query.add("foo");
            query.add(new JsonObject());
            JsonObject response = rdbConnection.executeSync(query);
            assertThat(response.get("t").getAsInt(), equalTo(1));
            assertThat(response.get("r").getAsJsonArray().get(0).getAsString(), equalTo("foo"));
            assertThat(response.get("n").getAsJsonArray().size(), equalTo(0));
        }
    }

}
