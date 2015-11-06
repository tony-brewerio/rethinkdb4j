package com.tonybr.rethinkdb4j.playground.v1;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import org.junit.Before;

public class PlaygroundNettyClientV1Test {

    public String host;
    public int port;

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
    public void testHandshake() throws Exception {
        String result = PlaygroundNettyClientV1.handshake(host, port);
        assertThat(result, equalTo("SUCCESS"));
    }

}
