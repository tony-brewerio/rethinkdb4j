package com.tonybr.rethinkdb4j.playground.v1;

import com.tonybr.rethinkdb4j.ql2.Protocol;
import com.tonybr.rethinkdb4j.ql2.Version;
import io.netty.util.concurrent.Promise;

public class RdbHandshake {

    public static final String RESPONSE_SUCCESS = "SUCCESS";

    /**
     * Driver only supports this version/protocol combination
     */
    public final Version version = Version.V0_4;
    public final Protocol protocol = Protocol.JSON;

    public final String authKey;
    public final Promise<String> responsePromise;

    public RdbHandshake(String authKey, Promise<String> responsePromise) {
        this.authKey = authKey;
        this.responsePromise = responsePromise;
    }

}
