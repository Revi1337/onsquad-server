package revi1337.onsquad.notification.infrastructure.redis;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RedisTopicTest {

    @Test
    @DisplayName("사용자 식별자(ID)를 전달하면, 정의된 포맷에 맞는 Redis Pub/Sub 채널명이 생성된다.")
    void format() {
        String userId = "1";
        RedisTopic redisTopic = RedisTopic.SSE_NOTIFICATION;

        String channel = redisTopic.format(userId);

        assertThat(channel).isEqualTo("channel:user:1:sse:notification");
    }
}
