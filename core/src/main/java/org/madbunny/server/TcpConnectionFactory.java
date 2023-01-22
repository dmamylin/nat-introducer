package org.madbunny.server;

public interface TcpConnectionFactory {
    TcpConnection create(TcpConnectionContext ctx);
}
