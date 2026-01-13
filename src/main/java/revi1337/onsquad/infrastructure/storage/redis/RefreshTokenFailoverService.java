package revi1337.onsquad.infrastructure.storage.redis;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import revi1337.onsquad.token.domain.model.RefreshTokenState;
import revi1337.onsquad.token.infrastructure.repository.ExpiringMapTokenRepository;

@Slf4j
@RequiredArgsConstructor
@Component
public class RefreshTokenFailoverService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ExpiringMapTokenRepository expiringMapTokenRepository;

    public void migrateTokensToRedis() {
        log.debug("Starting to Migrating Tokens to Redis");
        migrateToRedis(expiringMapTokenRepository.findAllState());
        expiringMapTokenRepository.deleteAll();
        log.debug("End to Migrating Tokens to Redis");
    }

    private void migrateToRedis(Map<String, RefreshTokenState> snapshots) {
        stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Map.Entry<String, RefreshTokenState> entry : snapshots.entrySet()) {
                String cacheName = entry.getKey();
                RefreshTokenState tokenState = entry.getValue();

                long remainingMillis = getRemainingTimeInMillis(tokenState);
                if (remainingMillis > 0) {
                    stringRedisTemplate.opsForHash().put(cacheName, "memberId", tokenState.memberId());
                    stringRedisTemplate.opsForHash().put(cacheName, "value", tokenState.value().value());
                    stringRedisTemplate.expire(cacheName, Duration.ofMillis(remainingMillis));
                }
            }
            return null;
        });
    }

    private long getRemainingTimeInMillis(RefreshTokenState tokenState) {
        return tokenState.expireTime() - Instant.now().toEpochMilli();
    }
}
