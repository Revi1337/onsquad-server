package revi1337.onsquad.notification.infrastructure.redis;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.container.RedisTestContainerSupport;
import revi1337.onsquad.notification.domain.NotificationDetail;
import revi1337.onsquad.notification.domain.NotificationTopic;
import revi1337.onsquad.notification.domain.model.NotificationMessage;

@ContextConfiguration(initializers = RedisTestContainerSupport.RedisInitializer.class)
class RedisNotificationMessageManagerTest extends ApplicationLayerTestSupport {

    @Autowired
    private RedisTemplate<String, NotificationMessage> redisTemplate;

    @Autowired
    private RedisNotificationMessageManager notificationMessageManager;

    @BeforeEach
    void setUp() {
        redisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    @Test
    @DisplayName("특정 사용자의 전용 알림 채널을 구독(Subscribe)하면 메시지 수신이 가능해진다.")
    void subscribe() {
        Long id = 1L;
        RedisTopic redisTopic = RedisTopic.SSE_NOTIFICATION;
        String channel = String.format("channel:user:%s:sse:notification", id);
        NotificationMessage notificationMessage = createUserNotificationMessage(1L, NotificationDetail.COMMENT, 1L, 2L);

        notificationMessageManager.subscribe(id, redisTopic);

        assertThat(redisTemplate.convertAndSend(channel, notificationMessage)).isEqualTo(1);
    }

    @Test
    @DisplayName("알림 채널 구독을 해제(Unsubscribe)하면 해당 채널로 발행된 메시지를 더 이상 수신하지 않는다.")
    void disableSubscribe() {
        Long id = 1L;
        RedisTopic redisTopic = RedisTopic.SSE_NOTIFICATION;
        String channel = String.format("channel:user:%s:sse:notification", id);
        NotificationMessage notificationMessage = createUserNotificationMessage(1L, NotificationDetail.COMMENT, 1L, 2L);
        notificationMessageManager.subscribe(id, redisTopic);

        notificationMessageManager.disableSubscribe(id, redisTopic);

        assertThat(redisTemplate.convertAndSend(channel, notificationMessage)).isZero();
    }

    @Test
    @DisplayName("특정 채널로 알림 메시지를 발행(Publish)하면 구독 중인 수신자 수만큼 메시지가 전달된다.")
    void publish() {
        Long id = 1L;
        RedisTopic redisTopic = RedisTopic.SSE_NOTIFICATION;
        NotificationMessage notificationMessage = createUserNotificationMessage(1L, NotificationDetail.COMMENT, 1L, 2L);
        notificationMessageManager.subscribe(id, redisTopic);

        Long receiver = notificationMessageManager.publish(id, redisTopic, notificationMessage);

        assertThat(receiver).isEqualTo(1);
    }

    private NotificationMessage createUserNotificationMessage(Long id, NotificationDetail detail, Long publisherId, Long receiverId) {
        return new NotificationMessage(id, NotificationTopic.USER, detail, publisherId, receiverId, false, new ObjectNode(JsonNodeFactory.instance));
    }
}
