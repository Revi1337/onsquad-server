package revi1337.onsquad.notification.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;
import revi1337.onsquad.notification.infrastructure.NotificationMessage;
import revi1337.onsquad.notification.infrastructure.sse.SseEmitterManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisNotificationMessageSubscriber implements MessageListener {

    private static final String CHANNEL_PREFIX = "channel:";
    private static final String EMPTY_SIGN = "";

    private final ObjectMapper defaultObjectMapper;
    private final SseEmitterManager sseEmitterManager;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String receiverId = new String(message.getChannel()).replaceFirst(CHANNEL_PREFIX, EMPTY_SIGN);
            NotificationMessage notificationMessage = defaultObjectMapper.readValue(new String(message.getBody()), NotificationMessage.class);
            sseEmitterManager.send(receiverId, notificationMessage);
        } catch (IOException e) {
            log.error("Failed to handle redis notification", e);
        }
    }
}
