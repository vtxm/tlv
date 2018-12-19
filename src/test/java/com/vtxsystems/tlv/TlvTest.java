package com.vtxsystems.tlv;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TlvTest {
    @Test
    public void test() {
        String expected = "asdf";
        byte[] bytes = expected.getBytes();
        Tlv tlv = new Tlv(1, bytes);
        byte[] tb = tlv.toByteArray();
        assertEquals(expected, new String(Tlv.fromByteArray(tb).getValue()));
    }
}

