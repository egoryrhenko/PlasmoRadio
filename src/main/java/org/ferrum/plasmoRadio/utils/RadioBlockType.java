package org.ferrum.plasmoRadio.utils;

public enum RadioBlockType {
    Microphone((byte) 0, "microphone"),
    Speaker((byte) 1, "speaker"),
    Locator((byte) 2, "locator");

    private final byte id;
    private final String name;

    RadioBlockType(byte id, String name) {
        this.id = id;
        this.name = name;
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

    public static RadioBlockType fromName(String name) {
        for (RadioBlockType type : values()) {
            if (type.name.equals(name)) return type;
        }
        throw new IllegalArgumentException("Unknown BlockType name: " + name)  ;
    }
}