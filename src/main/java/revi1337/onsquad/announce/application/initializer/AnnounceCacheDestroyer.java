package revi1337.onsquad.announce.application.initializer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.common.constant.Sign;
import revi1337.onsquad.infrastructure.redis.RedisCacheCleaner;

@Slf4j
@RequiredArgsConstructor
@Profile({"local", "default"})
@Component
public class AnnounceCacheDestroyer {

    private static final String DESTROY_KEY_PATTERN = String.format(CacheFormat.SIMPLE, CacheConst.CREW_ANNOUNCE + Sign.ASTERISK);
    private static final String DESTROY_KEY_START_LOG = "Try to Destroy Crew Announce Cache";

    private final StringRedisTemplate stringRedisTemplate;

    @EventListener(ContextClosedEvent.class)
    private void cleanAnnounceCaches(ContextClosedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            log.debug(DESTROY_KEY_START_LOG);
            RedisCacheCleaner.cleanup(stringRedisTemplate, DESTROY_KEY_PATTERN);
        }
    }
}
