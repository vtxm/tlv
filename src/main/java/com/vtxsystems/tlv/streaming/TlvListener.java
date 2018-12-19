package com.vtxsystems.tlv.streaming;

import com.vtxsystems.tlv.Tlv;

@FunctionalInterface
public interface TlvListener {
    void newTlv(Tlv tlv);
}