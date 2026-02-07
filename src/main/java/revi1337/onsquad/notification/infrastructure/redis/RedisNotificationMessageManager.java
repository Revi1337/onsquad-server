package revi1337.onsquad.notification.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import revi1337.onsquad.notification.domain.model.NotificationMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisNotificationMessageManager {

    private final RedisMessageListenerContainer listenerContainer;
    private final RedisNotificationMessageSubscriber messageSubscriber;
    private final RedisTemplate<String, NotificationMessage> redisTemplate;

    public void subscribe(Long id, RedisTopic topic) {
        ChannelTopic channelTopic = ChannelTopic.of(computeChannel(id, topic));
        listenerContainer.addMessageListener(messageSubscriber, channelTopic);
    }

    public void disableSubscribe(Long id, RedisTopic topic) {
        ChannelTopic channelTopic = ChannelTopic.of(computeChannel(id, topic));
        listenerContainer.removeMessageListener(messageSubscriber, channelTopic);
    }

    public Long publish(Long id, RedisTopic topic, NotificationMessage message) {
        String channel = computeChannel(id, topic);
        return redisTemplate.convertAndSend(channel, message);
    }

    private String computeChannel(Long id, RedisTopic topic) {
        return topic.format(String.valueOf(id));
    }
}
