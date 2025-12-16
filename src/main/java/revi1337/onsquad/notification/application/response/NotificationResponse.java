package revi1337.onsquad.notification.application.response;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDateTime;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;

public record NotificationResponse(
        Long id,
        Long receiverId,
        Long publisherId,
        NotificationTopic topic,
        NotificationDetail detail,
        LocalDateTime occurredAt,
        boolean read,
        JsonNode payload
) {

    public static NotificationResponse from(NotificationEntity notification, JsonNode payloadJson) {
        return new NotificationResponse(
                notification.getId(),
                notification.getReceiverId(),
                notification.getPublisherId(),
                notification.getTopic(),
                notification.getDetail(),
                notification.getOccurredAt(),
                notification.isRead(),
                payloadJson
        );
    }
}
