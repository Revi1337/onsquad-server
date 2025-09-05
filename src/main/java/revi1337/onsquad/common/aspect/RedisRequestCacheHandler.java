package revi1337.onsquad.common.aspect;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * RedisRequestCacheHandler For RequestCacheHandlerExecutionChain
 *
 * @see RequestCacheHandlerExecutionChain
 * @deprecated Redis is unnecessary due to single-instance usage, so this Class has been deprecated.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RedisRequestCacheHandler implements RequestCacheHandler {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Boolean isFirstRequest(String key, String value, long timeout, TimeUnit unit) {
        try {
            var valueOperations = stringRedisTemplate.opsForValue();
            return valueOperations.setIfAbsent(key, value, timeout, unit);
        } catch (RedisConnectionFailureException exception) {
            log.debug("[Redis 연결 실패] 다음 캐싱후보군을 적용합니다.");
            return null;
        }
    }
}
