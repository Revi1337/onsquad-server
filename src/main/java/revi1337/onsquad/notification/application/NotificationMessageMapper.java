package revi1337.onsquad.notification.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import revi1337.onsquad.notification.domain.Notification;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;
import revi1337.onsquad.notification.domain.model.NotificationMessage;

@Component
public class NotificationMessageMapper {

    private final ObjectMapper objectMapper;

    public NotificationMessageMapper(@Qualifier("defaultObjectMapper") ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public NotificationMessage from(Notification notification) {
        return new NotificationMessage(
                null,
                notification.getTopic(),
                notification.getDetail(),
                notification.getPublisherId(),
                notification.getReceiverId(),
                null,
                toJsonNode(notification.getPayload())
        );
    }

    public NotificationMessage from(NotificationEntity entity) {
        return new NotificationMessage(
                entity.getId(),
                entity.getTopic(),
                entity.getDetail(),
                entity.getPublisherId(),
                entity.getReceiverId(),
                entity.isRead(),
                toJsonNode(entity.getJson())
        );
    }

    private JsonNode toJsonNode(Object payload) {
        if (payload == null) {
            return objectMapper.createObjectNode();
        }
        if (payload instanceof JsonNode jsonNode) {
            return jsonNode;
        }
        if (payload instanceof String json) {
            try {
                return objectMapper.readTree(json);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON payload string", e);
            }
        }
        return objectMapper.valueToTree(payload);
    }
}
