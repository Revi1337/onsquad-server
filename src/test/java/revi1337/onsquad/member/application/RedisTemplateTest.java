package revi1337.onsquad.member.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import revi1337.onsquad.config.SpringActiveProfilesResolver;
import revi1337.onsquad.support.TestContainerSupport;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Redis 정상동작 테스트")
@DataRedisTest
@ActiveProfiles(resolver = SpringActiveProfilesResolver.class)
public class RedisTemplateTest extends TestContainerSupport {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String KEY_FORMAT = "onsquad:auth:mail:%s";
    private static final String EMAIL = "test@email.com";
    private static final String REDIS_KEY = String.format(KEY_FORMAT, EMAIL);

    @AfterEach
    void tearDown() {
        stringRedisTemplate.delete(REDIS_KEY);
    }

    @DisplayName("인증코드가 Redis 에 저장되는지 확인한다.")
    @Test
    public void redisTest() {
        // given
        String authCode = new RandomCodeGenerator().generate();
        stringRedisTemplate.opsForValue().set(REDIS_KEY, authCode, Duration.ofMinutes(3));

        // when
        String extractRedisValue = stringRedisTemplate.opsForValue().get(REDIS_KEY);

        // then
        assertThat(extractRedisValue).isEqualTo(authCode);
    }

}
