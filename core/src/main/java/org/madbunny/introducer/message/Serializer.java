package org.madbunny.introducer.message;

import org.madbunny.introducer.message.Message;

public interface Serializer {
    byte[] serialize(Message entity);
}
