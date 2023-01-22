package org.madbunny.introducer.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.util.function.Consumer;

public class MessageReader {
    private final static int BUFFER_SIZE = 8192;

    private final byte[] buffer = new byte[BUFFER_SIZE];
    private final ByteArrayOutputStream messageBuilder = new ByteArrayOutputStream(BUFFER_SIZE);
    private final Deserializer deserializer;

    public MessageReader(Deserializer deserializer) {
        this.deserializer = deserializer;
    }

    public void read(InputStream inputStream, MessageVisitor visitor) {
        while (true) {
            try {
                int bytesRead = inputStream.read(buffer);
                if (bytesRead > 0) {
                    onDataAcquire(bytesRead, data -> deserializer.deserialize(data, visitor));
                } else if (bytesRead < 0) {
                    return;
                }
            } catch (IOException ignored) {
                return;
            }
        }
    }

    private void onDataAcquire(int bytesRead, Consumer<byte[]> dataConsumer) {
        int begin = 0;
        for (int i = 0; i < bytesRead; i++) {
            if (buffer[i] == MessageWriter.RECORD_SEPARATOR) {
                messageBuilder.write(buffer, begin, i - begin);
                if (messageBuilder.size() > 0) {
                    dataConsumer.accept(messageBuilder.toByteArray());
                }
                messageBuilder.reset();
                begin = i + 1;
            }
        }
        if (begin < bytesRead) {
            messageBuilder.write(buffer, begin, buffer.length - begin);
        }
    }
}
