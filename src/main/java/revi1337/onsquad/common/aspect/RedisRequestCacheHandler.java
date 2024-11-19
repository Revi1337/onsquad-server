package revi1337.onsquad.common.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
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
