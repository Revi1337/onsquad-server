package revi1337.onsquad.category.presentation.converter;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;

public class HashtagTypeDeserializer extends StdDeserializer<HashtagType> {

    public HashtagTypeDeserializer() {
        super(HashtagType.class);
    }

    @Override
    public HashtagType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        TextNode textNode = p.getCodec().readTree(p);
        String hashtag = textNode.asText();
        return HashtagType.fromText(hashtag);
    }
}
