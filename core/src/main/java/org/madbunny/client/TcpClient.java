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

    public TcpClient(String remoteAddress, short remotePort) throws IOException {
        try (var client = SocketChannel.open()) {
            socket = client;
            var serverAddress = new InetSocketAddress(remoteAddress, remotePort);
            socket.configureBlocking(true);
            socket.bind(new InetSocketAddress(0));
            socket.connect(serverAddress);
        }
        output = new TcpSocketWriter(socket, WRITE_BUFFER_SIZE);
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
