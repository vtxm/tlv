package com.vtxsystems.tlv.streaming;

import com.vtxsystems.tlv.Tlv;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class TlvStreamTest {
    @Test
    public void test() {
        String expected = "asdflkjhg";
        AtomicInteger ai = new AtomicInteger(0);
        Tlv tlv = new Tlv(1, expected.getBytes());
        TlvStream tlvStream = new TlvStream();
        tlvStream.addTlvListener(t -> {
            ai.incrementAndGet();
            String s = new String(t.getValue());
            if (!s.equals(expected)) {
                throw new RuntimeException("not equals");
            }
        });
        byte[] bytes = tlv.toByteArray();
        int half = bytes.length / 2;
        byte[] h1 = new byte[half];
        byte[] h2 = new byte[bytes.length - half];
        System.arraycopy(bytes, 0, h1, 0, half);
        System.arraycopy(bytes, half, h2, 0, bytes.length - half);
        tlvStream.push(new byte[0]);
        tlvStream.push(h1);
        tlvStream.push(h2);
        assertEquals(1, ai.intValue());
    }

    @Test
    public void bufferSize() {
        String[] expected = new String[] { "asdflkjhg", "1" };
        AtomicInteger ai = new AtomicInteger(0);
        Tlv tlv1 = new Tlv(1, expected[0].getBytes());
        Tlv tlv2 = new Tlv(1, expected[1].getBytes());
        TlvStream tlvStream = new TlvStream(7);
        tlvStream.addTlvListener(t -> {
            String s = new String(t.getValue());
            if (!s.equals(expected[ai.intValue()])) {
                throw new RuntimeException("not equals");
            }
            ai.incrementAndGet();
        });
        tlvStream.push(tlv1.toByteArray());
        tlvStream.push(tlv2.toByteArray());
        assertEquals(2, ai.intValue());
    }
}