package org.madbunny.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TcpServer {
    private static final int MAX_CONNECTIONS = 20;
    private static final int SOCKET_CHECK_PERIOD_MS = 200;

    private final SocketAddress address;

    public TcpServer(int port) {
        address = new InetSocketAddress(port);
    }

    public void run(AtomicBoolean stopFlag, TcpConnectionFactory connectionFactory) {
        try (var server = ServerSocketChannel.open();
             var selector = Selector.open()) {
            server.configureBlocking(false);
            server.bind(address, MAX_CONNECTIONS);
            server.register(selector, SelectionKey.OP_ACCEPT);
            var worker = new ServerWorker(server, selector, connectionFactory, stopFlag);
            worker.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run(TcpConnectionFactory connectionFactory) {
        var unusedStopFlag = new AtomicBoolean(false);
        run(unusedStopFlag, connectionFactory);
    }

    private static class ServerWorker {
        private final TcpConnectionManager connectionManager;
        private final ServerSocketChannel server;
        private final Selector selector;
        private final AtomicBoolean stopFlag;

        ServerWorker(ServerSocketChannel server, Selector selector, TcpConnectionFactory connectionFactory, AtomicBoolean stopFlag) {
            this.connectionManager = new TcpConnectionManager(MAX_CONNECTIONS, connectionFactory);
            this.server = server;
            this.selector = selector;
            this.stopFlag = stopFlag;
        }

        void run() {
            try {
                while (!stopFlag.get()) {
                    if (selector.select(SOCKET_CHECK_PERIOD_MS) <= 0) {
                        continue;
                    }

                    for (var it = selector.selectedKeys().iterator(); it.hasNext();) {
                        var key = it.next();
                        it.remove();

                        if (key.isAcceptable() && !acceptConnection()) {
                            continue;
                        }

                        if (key.isReadable()) {
                            connectionManager.onDataAcquire((SocketChannel) key.channel());
                        }

                        if (!key.isValid()) {
                            connectionManager.onConnectionClose((SocketChannel) key.channel());
                        }
                    }
                }
            } catch (Exception e) {
                System.out.printf("Server stopped due to an exception: %s%n", e);
            } finally {
                connectionManager.closeConnections();
            }
        }

        private boolean acceptConnection() throws IOException {
            var socket = server.accept();
            socket.configureBlocking(false);
            socket.setOption(StandardSocketOptions.SO_KEEPALIVE, true);
            if (!connectionManager.onNewConnection(socket)) {
                return false;
            }
            socket.register(selector, SelectionKey.OP_READ);
            return true;
        }
    }
}
