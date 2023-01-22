import helpers.SimpleMessageVisitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.madbunny.introducer.message.Message;
import org.madbunny.introducer.message.MessageReader;
import org.madbunny.introducer.message.MessageVisitor;
import org.madbunny.introducer.message.MessageWriter;
import org.madbunny.introducer.message.json.JsonDeserializer;
import org.madbunny.introducer.message.json.JsonSerializer;
import org.madbunny.introducer.message.type.ServerAddress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestWriteMessage {
    private final JsonSerializer serializer = new JsonSerializer();
    private final JsonDeserializer deserializer = new JsonDeserializer();

    @Test
    public void serverAddress() {
        var visitor = new SimpleMessageVisitor();
        var expected = new ServerAddress("127.0.0.1", 80);
        writeAndRead(expected, visitor);
        Assertions.assertEquals(expected, visitor.serverAddress);
    }

    private void writeAndRead(Message message, MessageVisitor visitor) {
        try {
            var buffer = new ByteArrayOutputStream();
            var writer = new MessageWriter(serializer);
            writer.write(buffer, message);
            var reader = new MessageReader(deserializer);
            reader.read(new ByteArrayInputStream(buffer.toByteArray()), visitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
