package org.madbunny.introducer.message.type;

import org.madbunny.introducer.message.Message;

public record ServerAddress(
        String hostname,
        int port
) implements Message {
    @Override
    public MessageType type() {
        return MessageType.SERVER_ADDRESS;
    }
}
