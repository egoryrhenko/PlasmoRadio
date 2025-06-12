package org.ferrum.plasmoRadio.utils;

public enum BlockType {
    Microphone((byte) 0),
    Speaker((byte) 1);

    private final byte id;

    BlockType(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    public static BlockType fromId(byte id) {
        for (BlockType type : values()) {
            if (type.id == id) return type;
        }
        throw new IllegalArgumentException("Unknown BlockType id: " + id);
    }
}