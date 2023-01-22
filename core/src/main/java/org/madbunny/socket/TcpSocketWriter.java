package org.madbunny.socket;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class TcpSocketWriter extends OutputStream {
    private final SocketChannel socket;
    private final ByteBuffer buffer;

    public TcpSocketWriter(SocketChannel socket, int bufferSize) {
        this.socket = socket;
        buffer = ByteBuffer.allocate(bufferSize);
    }

    @Override
    public void write(int b) throws IOException {
        if (buffer.remaining() == 0) {
            unsafeFlush();
        }
        buffer.put((byte) b);
    }

    @Override
    public void write(byte[] data, int offset, int len) throws IOException {
        if (offset < 0 || offset >= data.length) {
            return;
        }
        len = Math.min(len, data.length - offset);
        while (offset < len) {
            if (buffer.remaining() == 0) {
                unsafeFlush();
            }
            int size = Math.min(buffer.remaining(), len - offset);
            buffer.put(data, offset, size);
            offset += size;
        }
    }

    @Override
    public void flush() throws IOException {
        if (buffer.position() > 0) {
            unsafeFlush();
        }
    }

    private void unsafeFlush() throws IOException {
        buffer.flip();
        while (buffer.remaining() > 0) {
            socket.write(buffer);
        }
        buffer.clear();
    }
}
