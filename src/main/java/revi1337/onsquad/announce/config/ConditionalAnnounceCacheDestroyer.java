package revi1337.onsquad.announce.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Profile({"local", "default"})
@Slf4j
@RequiredArgsConstructor
@Configuration
public class ConditionalAnnounceCacheDestroyer {

    private static final String DESTROY_KEY_PATTERN = "onsquad:crew:*:announces";
    private final RedisTemplate<String, Object> redisTemplate;

    @EventListener(ContextClosedEvent.class)
    private void cleanAnnounceCaches() {
        Cursor<byte[]> scanCursor = redisTemplate.getConnectionFactory()
                .getConnection()
                .scan(ScanOptions.scanOptions()
                        .match(DESTROY_KEY_PATTERN)
                        .build()
                );

        List<String> keys = new ArrayList<>();
        while (scanCursor.hasNext()) {
            keys.add(new String(scanCursor.next(), StandardCharsets.UTF_8));
        }

        if (!keys.isEmpty()) {
            log.debug("[Destroy Cached Crew Announces] keys : {}", keys);
            redisTemplate.delete(keys);
        }
    }
}
