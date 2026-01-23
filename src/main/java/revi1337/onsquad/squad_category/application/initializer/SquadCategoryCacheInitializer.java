package revi1337.onsquad.squad_category.application.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;

@Slf4j
@Profile({"local", "default"})
@Component
@RequiredArgsConstructor
public class SquadCategoryCacheInitializer {

    private static final String SQUAD_CATEGORY_PATTERN = "squad:*:categories";

    private final StringRedisTemplate stringRedisTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        String pattern = String.format(CacheFormat.SIMPLE, SQUAD_CATEGORY_PATTERN);
        RedisCacheEvictor.scanKeysAndUnlink(stringRedisTemplate, pattern);
        log.info("[Cache-Evict] Initializing Squad category caches on startup. Pattern: {}", pattern);
    }
}
