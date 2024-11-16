package io.jespen.lib;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ResSerializer extends StdSerializer<Message> {
    
    public ResSerializer() {
        this(null);
    }
  
    public ResSerializer(Class<Message> t) {
        super(t);
    }

    @Override
    public void serialize(Message message, JsonGenerator gen,
            SerializerProvider serializers) throws IOException {

        gen.writeStartObject();
        gen.writeStringField("src", message.headers().src());
        gen.writeStringField("dest", message.headers().dest());
        gen.writePOJOField("body", message.payload());
        gen.writeEndObject();
    }

}
