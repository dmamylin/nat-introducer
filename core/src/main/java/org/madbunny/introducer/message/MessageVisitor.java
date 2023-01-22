package org.madbunny.introducer.message;

import org.madbunny.introducer.message.type.MessageType;
import org.madbunny.introducer.message.type.ServerAddress;

import java.util.function.Function;

public class MessageVisitor {
    public void onServerAddress(ServerAddress message) {}

    public final void visit(String type, Function<Class<? extends Message>, Message> parser) {
        switch (parseType(type)) {
            case SERVER_ADDRESS -> onServerAddress((ServerAddress) parser.apply(ServerAddress.class));
        }
    }

    private static MessageType parseType(String data) {
        try {
            return MessageType.valueOf(data);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(String.format("Unknown message type: %s", data));
        }
    }
}
