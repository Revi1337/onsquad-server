package revi1337.onsquad.announce.application.initializer;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.common.constant.Sign;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;

@Slf4j
@Profile({"local", "default"})
@Component
@RequiredArgsConstructor
public class AnnounceCacheEvictor {

    private static final List<String> DESTROY_KEY_PATTERNS = List.of(
            String.format(CacheFormat.SIMPLE, CacheConst.CREW_ANNOUNCE + Sign.COLON + Sign.ASTERISK),
            String.format(CacheFormat.SIMPLE, CacheConst.CREW_ANNOUNCES + Sign.COLON + Sign.ASTERISK)
    );

    private final StringRedisTemplate stringRedisTemplate;

    @Order(2)
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        log.info("[Cache-Evict] Evicting Announce caches on startup. Patterns: {}", DESTROY_KEY_PATTERNS);
        RedisCacheEvictor.scanKeysAndUnlink(stringRedisTemplate, DESTROY_KEY_PATTERNS);
    }
}
