package org.ferrum.plasmoRadio.utils;

public enum RadioBlockType {
    Microphone((byte) 0),
    Speaker((byte) 1),
    Locator((byte) 2);

    private final byte id;

    RadioBlockType(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    public static RadioBlockType fromId(byte id) {
        for (RadioBlockType type : values()) {
            if (type.id == id) return type;
        }
        throw new IllegalArgumentException("Unknown BlockType id: " + id);
    }
}