package revi1337.onsquad.notification.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;
import revi1337.onsquad.notification.domain.repository.NotificationRepository;
import revi1337.onsquad.notification.infrastructure.NotificationMessage;
import revi1337.onsquad.notification.infrastructure.NotificationMessages;

@Component
@RequiredArgsConstructor
public class NotificationMessageRecoverer {

    private final NotificationRepository notificationRepository;
    private final NotificationMessageMapper notificationMessageMapper;

    public NotificationMessages recover(Long userId, Long lastEventId) {
        List<NotificationEntity> missedNotifications = notificationRepository.findAllByReceiverIdAndIdAfter(userId, lastEventId);
        List<NotificationMessage> messages = convertToMessages(missedNotifications);
        return new NotificationMessages(messages);
    }

    private List<NotificationMessage> convertToMessages(List<NotificationEntity> entities) {
        return entities.stream()
                .map(notificationMessageMapper::from)
                .toList();
    }
}
