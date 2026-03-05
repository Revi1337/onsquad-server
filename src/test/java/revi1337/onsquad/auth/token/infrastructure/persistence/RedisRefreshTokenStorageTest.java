package revi1337.onsquad.auth.token.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import revi1337.onsquad.auth.token.domain.model.RefreshToken;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;

@Import(RedisRefreshTokenStorage.class)
@ContextConfiguration(initializers = RedisTestContainerInitializer.class)
@DataRedisTest
class RedisRefreshTokenStorageTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisRefreshTokenStorage refreshTokenStorage;

    private final Long memberId = 1L;

    @BeforeEach
    void setUp() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    @Nested
    @DisplayName("리프레시 토큰 저장")
    class SaveToken {

        @Test
        @DisplayName("토큰을 저장하면 Redis에 지정된 키 형식으로 데이터가 기록된다.")
        void saveTokenInRedis() {
            String tokenValue = "refresh-token-value";
            Duration expireDuration = Duration.ofDays(1);
            Date expiredAt = new Date(Instant.now().plus(expireDuration).toEpochMilli());
            RefreshToken refreshToken = new RefreshToken(memberId, tokenValue, expiredAt); // Date 주입

            refreshTokenStorage.saveToken(memberId, refreshToken, expireDuration);

            assertSoftly(softly -> {
                String expectedKey = "onsquad:refresh-token:user:" + memberId;
                String savedValue = stringRedisTemplate.opsForValue().get(expectedKey);
                Long ttl = stringRedisTemplate.getExpire(expectedKey);

                softly.assertThat(savedValue).isEqualTo(tokenValue);
                softly.assertThat(ttl).isGreaterThan(0);
            });
        }
    }

    @Nested
    @DisplayName("리프레시 토큰 조회")
    class FindTokenBy {

        @Test
        @DisplayName("Redis에 키가 존재하면 Optional에 담아 반환한다.")
        void returnTokenWhenExists() {
            String tokenValue = "valid-token";
            Date expiredAt = new Date(Instant.now().plus(Duration.ofMinutes(5)).toEpochMilli());
            refreshTokenStorage.saveToken(memberId, new RefreshToken(memberId, tokenValue, expiredAt), Duration.ofMinutes(5));

            Optional<RefreshToken> result = refreshTokenStorage.findTokenBy(memberId);

            assertSoftly(softly -> {
                softly.assertThat(result).isPresent();
                softly.assertThat(result.get().value()).isEqualTo(tokenValue);
            });
        }

        @Test
        @DisplayName("존재하지 않는 memberId로 조회하면 빈 Optional을 반환한다.")
        void returnEmptyWhenNotFound() {
            Optional<RefreshToken> result = refreshTokenStorage.findTokenBy(999L);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("리프레시 토큰 삭제")
    class DeleteTokens {

        @Test
        @DisplayName("특정 회원의 토큰 키를 Redis에서 제거한다.")
        void deleteByMemberId() {
            Date expiredAt = new Date(Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli());
            refreshTokenStorage.saveToken(memberId, new RefreshToken(memberId, "token", expiredAt), Duration.ofMinutes(1));

            refreshTokenStorage.deleteTokenBy(memberId);

            String expectedKey = "onsquad:refresh-token:user:" + memberId;
            assertThat(stringRedisTemplate.hasKey(expectedKey)).isFalse();
        }

        @Test
        @DisplayName("패턴 매칭을 통해 모든 리프레시 토큰 키를 제거한다.")
        void deleteAllTokens() {
            Date expiredAt = new Date(Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli());
            refreshTokenStorage.saveToken(1L, new RefreshToken(1L, "t1", expiredAt), Duration.ofMinutes(1));
            refreshTokenStorage.saveToken(2L, new RefreshToken(2L, "t2", expiredAt), Duration.ofMinutes(1));
            stringRedisTemplate.opsForValue().set("onsquad:other:key", "value");

            refreshTokenStorage.deleteAll();

            assertSoftly(softly -> {
                softly.assertThat(stringRedisTemplate.hasKey("onsquad:refresh-token:user:1")).isFalse();
                softly.assertThat(stringRedisTemplate.hasKey("onsquad:refresh-token:user:2")).isFalse();
                softly.assertThat(stringRedisTemplate.hasKey("onsquad:other:key")).isTrue();
            });
        }
    }
}
