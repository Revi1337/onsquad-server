package revi1337.onsquad.announce.application.initializer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.common.config.ActuatorConfig.RedisHealthLoggingIndicator;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.common.constant.Sign;

@Profile({"local", "default"})
@Component
@Slf4j
@RequiredArgsConstructor
public class AnnounceCacheDestroyer {

    private static final String DESTROY_KEY_PATTERN = String.format(CacheFormat.SIMPLE, CacheConst.CREW_ANNOUNCE + Sign.ASTERISK);
    private static final String DESTROY_KEY_START_LOG = "Try to Destroy Crew Announce Cache";
    private static final String DESTROY_CACHE_SUCCESS_LOG_FORMAT = "Success Destroy Crew Announce Cache - size : {}";

    private final StringRedisTemplate stringRedisTemplate;

    @EventListener(ContextClosedEvent.class)
    private void cleanAnnounceCaches(ContextClosedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            log.debug(DESTROY_KEY_START_LOG);
            try (Cursor<byte[]> scanCursor = getRedisCursor()) {
                deleteKeys(collectKeysForDeletion(scanCursor));
            } catch (RuntimeException e) {
                log.error(RedisHealthLoggingIndicator.REDIS_HEALTH_CHECK_ERROR_LOG + ": cannot destroy redis-cache");
            }
        }
    }

    private Cursor<byte[]> getRedisCursor() {
        return stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .scan(ScanOptions.scanOptions()
                        .match(DESTROY_KEY_PATTERN)
                        .build()
                );
    }

    private List<String> collectKeysForDeletion(Cursor<byte[]> scanCursor) {
        List<String> keys = new ArrayList<>();
        while (scanCursor.hasNext()) {
            keys.add(new String(scanCursor.next(), StandardCharsets.UTF_8));
        }

        return keys;
    }

    private void deleteKeys(List<String> keys) {
        if (!keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
            log.debug(DESTROY_CACHE_SUCCESS_LOG_FORMAT, keys.size());
        }
    }
}
