package org.madbunny.server;

import java.io.OutputStream;

public record TcpConnectionContext(
        OutputStream output,
        String host,
        int port
) {}
