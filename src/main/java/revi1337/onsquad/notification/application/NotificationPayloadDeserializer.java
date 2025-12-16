package revi1337.onsquad.notification.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class NotificationPayloadDeserializer {

    private final ObjectMapper objectMapper;

    public NotificationPayloadDeserializer(@Qualifier("defaultObjectMapper") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public JsonNode deserialize(String jsonPayload) {
        try {
            return objectMapper.readTree(jsonPayload);
        } catch (JsonProcessingException e) {
            return objectMapper.createObjectNode();
        }
    }
}
