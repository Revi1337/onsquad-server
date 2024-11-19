package revi1337.onsquad.auth.domain.redis;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import revi1337.onsquad.auth.application.redis.RedisHashTokenOperation;
import revi1337.onsquad.auth.application.token.RefreshToken;
import revi1337.onsquad.config.SpringActiveProfilesResolver;
import revi1337.onsquad.support.TestContainerSupport;

@DisplayName("RedisTokenRepository 테스트")
@Import(RedisHashTokenOperation.class)
@DataRedisTest
@ActiveProfiles(resolver = SpringActiveProfilesResolver.class)
class RedisHashTokenRepositoryTest extends TestContainerSupport {

    @Autowired
    private RedisHashTokenOperation redisHashTokenRepository;

    @DisplayName("RefreshToken 이 저장되는지 확인한다.")
    @Test
    public void storeTemporaryRefreshToken() {
        // given
        RefreshToken refreshToken = RefreshToken.of(UUID.randomUUID().toString());
        Long id = 1L;
        Duration duration = Duration.ofSeconds(5);

        // when
        redisHashTokenRepository.storeTemporaryRefreshToken(refreshToken, id, duration);

        // then
        Optional<RefreshToken> findToken = redisHashTokenRepository.retrieveTemporaryRefreshToken(id);
        assertSoftly(softly -> {
            softly.assertThat(findToken).isPresent();
            softly.assertThat(findToken.get()).isEqualTo(refreshToken);
        });
    }

    @DisplayName("RefreshToken 이 잘 조회되는지 확인한다.")
    @Test
    public void retrieveTemporaryRefreshToken() {
        // given
        RefreshToken refreshToken = RefreshToken.of(UUID.randomUUID().toString());
        Long id = 1L;
        Duration duration = Duration.ofSeconds(5);
        redisHashTokenRepository.storeTemporaryRefreshToken(refreshToken, id, duration);

        // when
        Optional<RefreshToken> findToken = redisHashTokenRepository.retrieveTemporaryRefreshToken(id);

        // then
        assertSoftly(softly -> softly.assertThat(findToken).isPresent());
    }
}