package org.madbunny.client;

import org.madbunny.socket.TcpSocketWriter;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class TcpClient extends OutputStream implements Closeable {
    private static final int WRITE_BUFFER_SIZE = 8192;

    private final SocketChannel socket;
    private final OutputStream output;

    public TcpClient(String remoteAddress, int remotePort) throws IOException {
        socket = SocketChannel.open();
        try {
            socket.connect(new InetSocketAddress(remoteAddress, remotePort));
            output = new TcpSocketWriter(socket, WRITE_BUFFER_SIZE);
        } catch (Exception e) {
            socket.close();
            throw e;
        }
    }

    @Override
    public void write(int b) throws IOException {
        output.write(b);
    }

    @Override
    public void write(byte[] data, int offset, int len) throws IOException {
        output.write(data, offset, len);
    }

    @Override
    public void flush() throws IOException {
        output.flush();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }
}
