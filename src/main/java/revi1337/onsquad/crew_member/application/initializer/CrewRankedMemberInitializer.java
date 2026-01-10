package revi1337.onsquad.crew_member.application.initializer;

import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.infrastructure.redis.RedisCacheEvictor;

@Slf4j
@RequiredArgsConstructor
public class CrewRankedMemberInitializer {

    private static final String CURRENT_CREW_RANKED_PATTERN = "crew:*:rank-members:current";
    private static final String LAST_WEEK_CREW_RANKED_PATTERN = "crew:*:rank-members:last-week";

    private final StringRedisTemplate stringRedisTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        List<String> patterns = Stream.of(CURRENT_CREW_RANKED_PATTERN, LAST_WEEK_CREW_RANKED_PATTERN)
                .map(pattern -> String.format(CacheFormat.SIMPLE, pattern))
                .toList();

        RedisCacheEvictor.scanKeysAndUnlink(stringRedisTemplate, patterns);
        log.info("[Cache-Evict] Initializing Crew ranking caches on startup. Patterns: {}", patterns);
    }
}
