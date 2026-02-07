package revi1337.onsquad.notification.application.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import revi1337.onsquad.notification.application.NotificationMessageMapper;
import revi1337.onsquad.notification.domain.HeartbeatNotification;
import revi1337.onsquad.notification.domain.model.NotificationMessage;
import revi1337.onsquad.notification.infrastructure.sse.SseEmitterManager;

@Component
@RequiredArgsConstructor
public class NotificationHeartbeatScheduler {

    private final NotificationMessageMapper notificationMessageMapper;
    private final SseEmitterManager sseEmitterManager;

    @Scheduled(fixedRate = 45000)
    public void broadcastHeartbeat() {
        NotificationMessage notificationMessage = notificationMessageMapper.from(new HeartbeatNotification());
        sseEmitterManager.broadcast(notificationMessage);
    }
}
