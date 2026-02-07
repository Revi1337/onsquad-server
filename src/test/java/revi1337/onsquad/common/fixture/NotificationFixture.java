package revi1337.onsquad.common.fixture;

import java.time.LocalDateTime;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;

public class NotificationFixture {

    public static NotificationEntity createUserNotification(
            Long receiverId,
            Long publisherId,
            boolean read,
            NotificationDetail detail,
            LocalDateTime occurredAt
    ) {
        return NotificationEntity.builder()
                .receiverId(receiverId)
                .publisherId(publisherId)
                .read(read)
                .topic(NotificationTopic.USER)
                .detail(detail)
                .json("{\"message\": \"hello\", \"count\": 1}")
                .occurredAt(occurredAt)
                .build();
    }
}
