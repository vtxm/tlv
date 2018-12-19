package com.vtxsystems.tlv.streaming;

import com.vtxsystems.tlv.Tlv;

import java.util.ArrayList;
import java.util.List;

import static com.vtxsystems.tlv.Tlv.LENGTH_SIZE;
import static com.vtxsystems.tlv.Tlv.TYPE_SIZE;

public class TlvStream {
    public final static int DEFAULT_BUFFER_TRIM_THRESHOLD = 65536;

    private final int bufferTrimThreshold;
    private byte[] buffer;
    private int position = 0;

    private final List<TlvListener> tlvListeners = new ArrayList<>();

    public TlvStream() {
        this(DEFAULT_BUFFER_TRIM_THRESHOLD);
    }

    public TlvStream(int bufferTrimThreshold) {
        this.bufferTrimThreshold = bufferTrimThreshold;
        this.buffer = new byte[bufferTrimThreshold];
    }

    public TlvStream addTlvListener(TlvListener listener) {
        tlvListeners.add(listener);
        return this;
    }

    private void realloc(int size) {
        byte[] nb = new byte[size];
        int sz = size > buffer.length ? buffer.length : size;
        System.arraycopy(buffer, 0, nb, 0, sz);
        buffer = nb;
    }

    public void push(byte[] data) {
        if (data.length > buffer.length - position) {
            realloc(position + data.length);
        } else if (data.length + position < bufferTrimThreshold) {
            realloc(bufferTrimThreshold);
        }
        System.arraycopy(data, 0, buffer, position, data.length);
        position += data.length;
        while (position >= TYPE_SIZE + LENGTH_SIZE) {
            int len = position;
            checkNewValue();
            if (len == position) {
                break;
            }
        }
    }

    private void checkNewValue() {
        byte type = buffer[0];
        int size = buffer[1] << 24 | (buffer[2] & 0xFF) << 16 | (buffer[3] & 0xFF) << 8 | (buffer[4] & 0xFF);
        if (position >= TYPE_SIZE + LENGTH_SIZE + size) {
            byte[] v = new byte[size];
            System.arraycopy(buffer, TYPE_SIZE + LENGTH_SIZE, v, 0, size);
            position -= (TYPE_SIZE + LENGTH_SIZE + size);
            System.arraycopy(buffer, TYPE_SIZE + LENGTH_SIZE + size, buffer, 0, position);
            onNewValue(new Tlv(type, v));
        }
    }

    private void onNewValue(Tlv tlv) {
        for (TlvListener listener : tlvListeners) {
            listener.newTlv(tlv);
        }
    }
}