package revi1337.onsquad.crew_member.application.initializer;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardKeyMapper;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;

@Slf4j
@Profile({"local", "default"})
@Component
@RequiredArgsConstructor
public class CrewLeaderboardCacheEvictor {

    public static final List<String> DESTROY_KEY_PATTERNS = List.of(
            CrewLeaderboardKeyMapper.getLeaderboardPattern(),
            CrewLeaderboardKeyMapper.getLeaderboardSnapshotPattern()
    );

    private final StringRedisTemplate stringRedisTemplate;

    @Order(1)
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        log.info("[Cache-Evict] Evicting Crew ranking caches on startup. Patterns: {}", DESTROY_KEY_PATTERNS);
        try {
            RedisCacheEvictor.scanKeysAndUnlink(stringRedisTemplate, DESTROY_KEY_PATTERNS);
        } catch (RuntimeException e) {
            log.error("[Cache-Evict] Failed to evict caches on startup. Patterns: {}, {}: {}",
                    DESTROY_KEY_PATTERNS, e.getClass().getSimpleName(), e.getMessage(), e);
        }
    }
}
