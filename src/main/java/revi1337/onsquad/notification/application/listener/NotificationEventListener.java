package revi1337.onsquad.notification.application.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import revi1337.onsquad.notification.application.NotificationMessageMapper;
import revi1337.onsquad.notification.application.NotificationPersister;
import revi1337.onsquad.notification.application.NotificationService;
import revi1337.onsquad.notification.domain.Notification;
import revi1337.onsquad.notification.domain.entity.NotificationEntity;
import revi1337.onsquad.notification.domain.model.NotificationMessage;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationPersister notificationPersister;
    private final NotificationMessageMapper notificationMessageMapper;
    private final NotificationService notificationService;

    @Async("notificationExecutor")
    @EventListener
    public void onNotification(Notification notification) {
        NotificationEntity notificationEntity = notificationPersister.persist(notification);
        NotificationMessage notificationMessage = notificationMessageMapper.from(notificationEntity);
        notificationService.sendMessage(notificationMessage);
    }
}
