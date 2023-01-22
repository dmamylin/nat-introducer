package org.madbunny.introducer.server;

import org.madbunny.introducer.message.MessageReader;
import org.madbunny.introducer.message.MessageVisitor;
import org.madbunny.introducer.message.MessageWriter;
import org.madbunny.introducer.message.json.JsonDeserializer;
import org.madbunny.introducer.message.json.JsonSerializer;
import org.madbunny.server.TcpConnection;
import org.madbunny.server.TcpConnectionContext;

import java.io.InputStream;

public class Connection implements TcpConnection  {
    private final TcpConnectionContext ctx;
    private final MessageWriter writer = new MessageWriter(new JsonSerializer());
    private final MessageReader reader = new MessageReader(new JsonDeserializer());
    private final MessageVisitor handler = new MessageHandler();

    Connection(TcpConnectionContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onDataReceive(InputStream inputStream) {
        reader.read(inputStream, handler);
//        try {
//            writer.write(new ServerAddress(ctx.host(), ctx.port()));
//        } catch (IOException ignored) {}
    }

    @Override
    public void onClose() {
        System.out.println("Gonna be closed!");
    }

    private static final class MessageHandler extends MessageVisitor {
    }
}
