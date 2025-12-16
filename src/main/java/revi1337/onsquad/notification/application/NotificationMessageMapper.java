package revi1337.onsquad.notification.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.notification.domain.Notification;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;
import revi1337.onsquad.notification.infrastructure.NotificationMessage;

@Component
@RequiredArgsConstructor
public class NotificationMessageMapper {

    private final ObjectMapper defaultObjectMapper;

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
            return defaultObjectMapper.createObjectNode();
        }
        if (payload instanceof JsonNode jsonNode) {
            return jsonNode;
        }
        if (payload instanceof String json) {
            try {
                return defaultObjectMapper.readTree(json);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON payload string", e);
            }
        }
        return defaultObjectMapper.valueToTree(payload);
    }
}
