package com.tonybr.rethinkdb4j.playground.v1;

import com.tonybr.rethinkdb4j.ql2.Protocol;
import com.tonybr.rethinkdb4j.ql2.Version;
import io.netty.util.concurrent.Promise;

public class RdbHandshake extends RdbRequest<String> {
    
    public final Version version;
    public final Protocol protocol;

    public RdbHandshake(Promise<String> promise, Version version, Protocol protocol) {
        super(0L, promise);
        this.version = version;
        this.protocol = protocol;
    }
    
}
