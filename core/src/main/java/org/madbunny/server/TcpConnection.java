package org.madbunny.server;

import java.io.InputStream;

public interface TcpConnection {
    void onDataReceive(InputStream inputStream);
    void onClose();
}
