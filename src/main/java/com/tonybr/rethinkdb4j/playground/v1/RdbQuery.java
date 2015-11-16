package com.tonybr.rethinkdb4j.playground.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.util.concurrent.Promise;

public class RdbQuery {
    public long seq;
    public JsonElement query;
    public Promise<JsonObject> responsePromise;

    public RdbQuery(long seq, JsonElement query, Promise<JsonObject> responsePromise) {
        this.seq = seq;
        this.query = query;
        this.responsePromise = responsePromise;
    }
}
