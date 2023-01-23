package org.madbunny.server;

import org.madbunny.socket.TcpSocketWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

class TcpConnectionWrapper {
    private static final int BUFFER_SIZE = 64;

    private final ByteBuffer socketReadBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    private final byte[] buffer = new byte[BUFFER_SIZE];
    private final SocketChannel socket;
    private final TcpConnection connection;
    private final String host;
    private final int port;

    TcpConnectionWrapper(SocketChannel socket, TcpConnectionFactory connectionFactory) throws IOException {
        this.socket = socket;
        var remoteAddress = (InetSocketAddress) socket.getRemoteAddress();
        host = remoteAddress.getAddress().getHostAddress();
        port = remoteAddress.getPort();
        var ctx = new TcpConnectionContext(new TcpSocketWriter(socket, BUFFER_SIZE), host, port);
        connection = connectionFactory.create(ctx);
        System.out.printf("New connection: %s:%d%n", host, port);
    }

    public boolean read() {
        try {
            while (true) {
                var bytesRead = socket.read(socketReadBuffer);
                if (bytesRead <= 0) {
                    break;
                }
                socketReadBuffer.flip();
                socketReadBuffer.get(buffer, 0, bytesRead);
                connection.onDataReceive(new ByteArrayInputStream(buffer));
                socketReadBuffer.clear();
            }
        } catch (IOException ignored) {
            return false;
        }
        return true;
    }

    public void close() {
        connection.onClose();
        try { socket.close(); } catch (IOException ignored) {}
        System.out.printf("Connection closed: %s:%d%n", host, port);
    }
}
