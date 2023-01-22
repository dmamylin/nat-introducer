package org.madbunny.introducer.message;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class MessageWriter {
    final static byte RECORD_SEPARATOR = 0x1E;

    private final Serializer serializer;

    public MessageWriter(Serializer serializer) {
        this.serializer = serializer;
    }

    public int write(OutputStream outputStream, Message message) throws IOException {
        var serialized = serializer.serialize(message);
        outputStream.write(serialized);
        outputStream.write(RECORD_SEPARATOR);
        outputStream.flush();
        return serialized.length + 1;
    }
}
