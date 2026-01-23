package revi1337.onsquad.notification.application;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.notification.domain.Notification;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;
import revi1337.onsquad.notification.domain.repository.NotificationRepository;

@Component
@RequiredArgsConstructor
public class NotificationPersister {

    private final ObjectMapper defaultObjectMapper;
    private final NotificationRepository notificationRepository;

    @Transactional(propagation = REQUIRES_NEW)
    public NotificationEntity persist(Notification notification) {
        try {
            return notificationRepository.save(NotificationEntity.builder()
                    .publisherId(notification.getPublisherId())
                    .receiverId(notification.getReceiverId())
                    .topic(notification.getTopic())
                    .detail(notification.getDetail())
                    .json(defaultObjectMapper.writeValueAsString(notification.getPayload()))
                    .build());
        } catch (Exception e) {
            throw new RuntimeException("cannot persist notification", e);
        }
    }
}
