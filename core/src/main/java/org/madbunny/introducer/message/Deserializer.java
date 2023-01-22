package org.madbunny.introducer.message;

public interface Deserializer {
    void deserialize(byte[] data, MessageVisitor visitor);
}
