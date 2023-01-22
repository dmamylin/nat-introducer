package org.madbunny.introducer.server;

import org.madbunny.server.TcpServer;

public class Application {
    public static void main(String[] args) {
        var server = new TcpServer(17051);
        server.run(Connection::new);
    }
}
