package com.vtxsystems.tlv;

public class Tlv {
    public final static int TYPE_SIZE = Byte.BYTES;
    public final static int LENGTH_SIZE = Integer.BYTES;

    private final byte type;
    private final byte[] value;

    public Tlv(int type, byte[] value) {
        if (type < Byte.MIN_VALUE || type > Byte.MAX_VALUE) {
            throw new IllegalArgumentException("type argument must be 1 byte size");
        }
        this.type = (byte) type;
        this.value = value;
    }

    public byte getType() {
        return type;
    }

    public int getLength() {
        return value.length;
    }

    public byte[] getValue() {
        return value;
    }

    public byte[] toByteArray() {
        byte[] bytes = new byte[TYPE_SIZE + LENGTH_SIZE + value.length];
        byte[] length = new byte[]{
                (byte) (value.length >> 24),
                (byte) (value.length >> 16),
                (byte) (value.length >> 8),
                (byte) value.length
        };
        System.arraycopy(new byte[] { type }, 0, bytes, 0, TYPE_SIZE);
        System.arraycopy(length, 0, bytes, TYPE_SIZE, LENGTH_SIZE);
        System.arraycopy(value, 0, bytes, TYPE_SIZE + LENGTH_SIZE, value.length);
        return bytes;
    }

    public static Tlv fromByteArray(byte[] bytes) {
        if (bytes.length < TYPE_SIZE + LENGTH_SIZE) {
            throw new RuntimeException("Insufficient length.");
        }
        byte type = bytes[0];
        int size = bytes[1] << 24 | (bytes[2] & 0xFF) << 16 | (bytes[3] & 0xFF) << 8 | (bytes[4] & 0xFF);
        if (size != (bytes.length - TYPE_SIZE - LENGTH_SIZE)) {
            throw new RuntimeException("Byte array size does not match value size");
        }
        byte[] value = new byte[size];
        System.arraycopy(bytes, TYPE_SIZE + LENGTH_SIZE, value, 0, size);
        return new Tlv(type, value);
    }
}