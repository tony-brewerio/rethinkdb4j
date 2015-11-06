package com.tonybr.rethinkdb4j.ql2;

public enum Version {
    V0_1(0x3f61ba36), // Authorization key during handshake
    V0_2(0x723081e1), // Authorization key during handshake
    V0_3(0x5f75e83e), // Authorization key and protocol during handshake
    V0_4(0x400c2d20); // Queries execute in parallel

    public final int val;

    Version(int val) {
        this.val = val;
    }
}
