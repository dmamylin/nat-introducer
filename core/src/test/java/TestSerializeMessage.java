import helpers.SimpleMessageVisitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.madbunny.introducer.message.json.JsonDeserializer;
import org.madbunny.introducer.message.json.JsonSerializer;
import org.madbunny.introducer.message.type.ServerAddress;

public class TestSerializeMessage {
    private final JsonSerializer serializer = new JsonSerializer();
    private final JsonDeserializer deserializer = new JsonDeserializer();

    @Test
    public void serverAddress() {
        var visitor = new SimpleMessageVisitor();
        var expected = new ServerAddress("127.0.0.1", 80);
        var bytes = serializer.serialize(expected);
        deserializer.deserialize(bytes, visitor);
        Assertions.assertEquals(expected, visitor.serverAddress);
    }
}
