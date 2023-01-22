package org.madbunny.server;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

class TcpConnectionManager {
    private final Map<SocketChannel, TcpConnectionWrapper> connections = new HashMap<>();
    private final int maxConnections;
    private final TcpConnectionFactory connectionFactory;

    TcpConnectionManager(int maxConnections, TcpConnectionFactory connectionFactory) {
        this.maxConnections = maxConnections;
        this.connectionFactory = connectionFactory;
    }

    public boolean onNewConnection(SocketChannel socket) {
        if (connections.size() == maxConnections) {
            try { socket.close(); } catch (IOException ignored) {}
            return false;
        }
        try {
            var connection = new TcpConnectionWrapper(socket, connectionFactory);
            connections.put(socket, connection);
            System.out.printf("Number of connections: %d/%d%n", connections.size(), maxConnections);
            return true;
        } catch (IOException ignored) {}
        return false;
    }

    public void onDataAcquire(SocketChannel socket) {
        var connection = connections.get(socket);
        if (connection != null && !connection.read()) {
            connection.close();
            connections.remove(socket);
        }
    }

    public void onConnectionClose(SocketChannel socket) {
        var connection = connections.get(socket);
        if (connection != null) {
            connection.close();
            connections.remove(socket);
        }
    }

    public void closeConnections() {
        connections.forEach((socket, connection) -> connection.close());
    }
}
