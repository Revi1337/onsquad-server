package revi1337.onsquad.auth.domain.redis;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import revi1337.onsquad.auth.domain.vo.RefreshToken;
import revi1337.onsquad.config.SpringActiveProfilesResolver;
import revi1337.onsquad.support.TestContainerSupport;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("RedisTokenRepository 테스트")
@Import(RedisTokenRepository.class)
@DataRedisTest
@ActiveProfiles(resolver = SpringActiveProfilesResolver.class)
class RedisTokenRepositoryTest extends TestContainerSupport {

    @Autowired private StringRedisTemplate stringRedisTemplate;
    @Autowired private RedisTokenRepository redisTokenRepository;

    @DisplayName("RefreshToken 이 저장되는지 확인한다.")
    @Test
    public void storeTemporaryRefreshToken() {
        // given
        RefreshToken refreshToken = RefreshToken.of(UUID.randomUUID().toString());
        Long id = 1L;
        Duration duration = Duration.ofSeconds(5);

        // when
        redisTokenRepository.storeTemporaryRefreshToken(refreshToken, id, duration);

        // then
        String extractedRefreshToken = stringRedisTemplate.opsForValue().get(refreshToken.value());
        assertThat(extractedRefreshToken).isNotNull();
    }

    @DisplayName("RefreshToken 이 잘 조회되는지 확인한다.")
    @Test
    public void retrieveTemporaryRefreshToken() {
        // given
        RefreshToken refreshToken = RefreshToken.of(UUID.randomUUID().toString());
        Long id = 1L;
        Duration duration = Duration.ofSeconds(5);
        redisTokenRepository.storeTemporaryRefreshToken(refreshToken, id, duration);

        // when
        Optional<Long> tokenOwnerId = redisTokenRepository.retrieveTemporaryRefreshToken(refreshToken);

        // then
        assertSoftly(softly -> {
            softly.assertThat(tokenOwnerId).isPresent();
            softly.assertThat(tokenOwnerId.get()).isEqualTo(1L);
        });
    }
}