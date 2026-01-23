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
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;

@Slf4j
@Profile({"local", "default"})
@Component
@RequiredArgsConstructor
public class AnnounceCacheDestroyer {

    private static final String DESTROY_KEY_PATTERN = String.format(CacheFormat.SIMPLE, CacheConst.CREW_ANNOUNCE + Sign.ASTERISK);

    private final StringRedisTemplate stringRedisTemplate;

    @EventListener(ContextClosedEvent.class)
    private void cleanAnnounceCaches(ContextClosedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            log.debug("Try to Destroy Crew Announce Cache");
            RedisCacheEvictor.scanKeysAndUnlink(stringRedisTemplate, DESTROY_KEY_PATTERN);
        }
    }
}
