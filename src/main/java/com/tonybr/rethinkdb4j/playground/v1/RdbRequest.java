package com.tonybr.rethinkdb4j.playground.v1;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

public class RdbRequest<R> {

    public final long id;
    public final Promise<R> promise;

    public RdbRequest(long id, Promise<R> promise) {
        this.id = id;
        this.promise = promise;
    }
    
}
