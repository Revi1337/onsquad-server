package revi1337.onsquad.notification.infrastructure.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RedisTopic {

    SSE_NOTIFICATION("sse-notification", "channel:user:%s:sse:notification");

    private final String text;
    private final String redisChannelFormat;

    public String format(String... args) {
        return String.format(redisChannelFormat, args);
    }
}
