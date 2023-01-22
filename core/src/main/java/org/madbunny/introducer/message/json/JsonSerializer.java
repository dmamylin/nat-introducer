package org.madbunny.introducer.message.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.madbunny.introducer.message.Message;
import org.madbunny.introducer.message.Serializer;

import java.io.ByteArrayOutputStream;

public class JsonSerializer implements Serializer {
    static final byte SEPARATOR = '|';

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public byte[] serialize(Message message) {
        byte[] separator = {SEPARATOR};
        var type = message.type().name().getBytes();
        var payload = serializeMessage(message);

        var buffer = new ByteArrayOutputStream(type.length + payload.length + 1);
        buffer.writeBytes(type);
        buffer.writeBytes(separator);
        buffer.writeBytes(payload);
        return buffer.toByteArray();
    }

    private byte[] serializeMessage(Message message) {
        try {
            return mapper.writeValueAsBytes(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
