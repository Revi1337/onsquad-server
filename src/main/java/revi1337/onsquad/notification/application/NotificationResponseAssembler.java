package revi1337.onsquad.notification.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.notification.application.response.NotificationResponse;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;

@RequiredArgsConstructor
@Component
public class NotificationResponseAssembler {

    private final NotificationPayloadDeserializer deserializer;

    public NotificationResponse assemble(NotificationEntity entity) {
        return NotificationResponse.from(entity, deserializer.deserialize(entity.getJson()));
    }
}
