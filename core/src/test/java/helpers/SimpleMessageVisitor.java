package helpers;

import org.madbunny.introducer.message.MessageVisitor;
import org.madbunny.introducer.message.type.ServerAddress;

public class SimpleMessageVisitor extends MessageVisitor {
    public ServerAddress serverAddress;

    @Override
    public void onServerAddress(ServerAddress message) {
        serverAddress = message;
    }
}
