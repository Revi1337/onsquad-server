package revi1337.onsquad.notification.application;

import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import revi1337.onsquad.notification.domain.ConnectionNotification;
import revi1337.onsquad.notification.infrastructure.NotificationMessage;
import revi1337.onsquad.notification.infrastructure.redis.RedisNotificationMessageManager;
import revi1337.onsquad.notification.infrastructure.redis.RedisTopic;
import revi1337.onsquad.notification.infrastructure.sse.NamedSseEmitter;
import revi1337.onsquad.notification.infrastructure.sse.SseEmitterManager;
import revi1337.onsquad.notification.infrastructure.sse.SseTopic;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SseEmitterManager sseEmitterManager;
    private final NotificationMessageMapper notificationMessageMapper;
    private final NotificationMessageRecoverer notificationMessageRecoverer;
    private final RedisNotificationMessageManager redisMessageManager;

    public NamedSseEmitter connect(Long userId, @Nullable Long lastEventId) {
        NamedSseEmitter emitter = sseEmitterManager.createEmitter(userId, SseTopic.USER,
                () -> redisMessageManager.disableSubscribe(userId, RedisTopic.SSE_NOTIFICATION));
        sseEmitterManager.send(emitter, notificationMessageMapper.from(new ConnectionNotification()));
        sseEmitterManager.sends(emitter, notificationMessageRecoverer.recover(userId, lastEventId));
        redisMessageManager.subscribe(userId, RedisTopic.SSE_NOTIFICATION);
        return emitter;
    }

    public void sendMessage(NotificationMessage message) {
        Long receiverId = message.receiverId();
        redisMessageManager.publish(receiverId, RedisTopic.SSE_NOTIFICATION, message);
    }
}
