package revi1337.onsquad.squad_category.application.initializer;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;

@Slf4j
@Profile({"local", "default"})
@Component
@RequiredArgsConstructor
public class SquadCategoryCacheEvictor {

    private static final List<String> DESTROY_KEY_PATTERNS = List.of(String.format(CacheFormat.SIMPLE, "squad:*:categories"));

    private final StringRedisTemplate stringRedisTemplate;

    @Order(3)
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        log.info("[Cache-Evict] Evicting Squad category caches on startup. Patterns: {}", DESTROY_KEY_PATTERNS);
        RedisCacheEvictor.scanKeysAndUnlink(stringRedisTemplate, DESTROY_KEY_PATTERNS);
    }
}
