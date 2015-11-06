package com.tonybr.rethinkdb4j.ql2;

public enum Protocol {
    PROTOBUF(0x271ffc41),
    JSON(0x7e6970c7);

    public final int val;

    Protocol(int val) {
        this.val = val;
    }
}
