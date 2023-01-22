package org.madbunny.introducer.message.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.madbunny.introducer.message.Deserializer;
import org.madbunny.introducer.message.Message;
import org.madbunny.introducer.message.MessageVisitor;

import java.io.IOException;
import java.util.ArrayList;

public class JsonDeserializer implements Deserializer {
    private final static String[] FIELD_NAMES = new String[]{"type", "content"};

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void deserialize(byte[] data, MessageVisitor visitor) {
        var fields = parseFields(data);
        visitor.visit(fields.get(0), klass -> parseMessage(fields.get(1), klass));
    }

    private Message parseMessage(String data, Class<? extends Message> klass) {
        try {
            return mapper.readValue(data, klass);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Unable to parse message due to an error: %s", e.getMessage()));
        }
    }

    private static int findSeparator(byte[] data, int offset) {
        for (int i = offset; i < data.length; i++) {
            if (data[i] == JsonSerializer.SEPARATOR) {
                return i;
            }
        }
        return -1;
    }

    private static ArrayList<String> parseFields(byte[] data) {
        int begin;
        int end = -1;
        var fields = new ArrayList<String>(FIELD_NAMES.length);
        for (int i = 0; i < FIELD_NAMES.length; i++) {
            begin = end + 1;
            end = i + 1 == FIELD_NAMES.length ? data.length : findSeparator(data, begin);
            if (end <= begin) {
                throw new RuntimeException(String.format("Unable to determine the %s of a message", FIELD_NAMES[i]));
            }
            fields.add(new String(data, begin, end - begin));
        }
        return fields;
    }
}
