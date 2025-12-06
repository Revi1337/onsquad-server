package revi1337.onsquad.infrastructure.redis;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.common.TestContainerSupport;

@ImportAutoConfiguration(RedisAutoConfiguration.class)
@ContextConfiguration(classes = RedisRequestCacheHandler.class)
@ExtendWith(SpringExtension.class)
class RedisRequestCacheHandlerTest extends TestContainerSupport {

    @Autowired
    private RedisRequestCacheHandler redisRequestCacheHandler;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @BeforeEach
    void setUp() {
        stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
    }

    @Test
    @DisplayName("처음 시도하는 요청이면 true 를 반환한다.")
    void success1() {
        String testKey = "testKey";
        String testValue = "testValue";
        long timeout = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        Boolean firstRequest = redisRequestCacheHandler.isFirstRequest(testKey, testValue, timeout, timeUnit);

        assertThat(firstRequest).isTrue();
    }

    @Test
    @DisplayName("만료시간 안에 2번 이상 동일한 요청을 보내면 false 를 반환한다.")
    void success2() {
        String testKey = "testKey";
        String testValue = "testValue";
        long timeout = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        stringRedisTemplate.opsForValue().set(testKey, testValue, timeout, timeUnit);

        Boolean firstRequest = redisRequestCacheHandler.isFirstRequest(testKey, testValue, timeout, timeUnit);

        assertThat(firstRequest).isFalse();
    }
}
