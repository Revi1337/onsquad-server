package revi1337.onsquad.crew_member.application.initializer;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardKeyMapper;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;

@Slf4j
@Profile({"local", "default"})
@Component
@RequiredArgsConstructor
public class CrewLeaderboardCacheEvictor {

    private final StringRedisTemplate stringRedisTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        List<String> patterns = List.of(CrewLeaderboardKeyMapper.getLeaderboardPattern(), CrewLeaderboardKeyMapper.getLeaderboardSnapshotPattern());
        RedisCacheEvictor.scanKeysAndUnlink(stringRedisTemplate, patterns);
        log.info("[Cache-Evict] Initializing Crew ranking caches on startup. Patterns: {}", patterns);
    }
}
