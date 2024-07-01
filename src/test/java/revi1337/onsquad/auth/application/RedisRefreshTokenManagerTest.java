package revi1337.onsquad.auth.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import revi1337.onsquad.auth.domain.redis.RedisTokenRepository;
import revi1337.onsquad.auth.domain.vo.RefreshToken;
import revi1337.onsquad.support.TestContainerSupport;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

/**
 * TODO TokenProperties 를 Mocking 하여 Stubbing 해야하는데... 아무리 찾아봐도 방법이 나오지않는다.
 *  Reflection 을 사용해서 할 수 있다고는하는데.. 시간관계상 슬라이스테스트 대신 SpringBootTest 로 대체한다.
 */
@SpringBootTest
@DisplayName("RedisRefreshTokenManager 테스트")
class RedisRefreshTokenManagerTest extends TestContainerSupport {

    @Autowired private StringRedisTemplate stringRedisTemplate;
    @Autowired private RedisTokenRepository redisTokenRepository;
    @Autowired private RedisRefreshTokenManager redisRefreshTokenManager;

    @AfterEach
    void tearDown() {
        stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
    }

    @DisplayName("RefreshToken 이 잘 저장되는지 확인한다.")
    @Test
    public void storeTemporaryToken() {
        // given
        RefreshToken refreshToken = RefreshToken.of(UUID.randomUUID().toString());
        Long id = 2L;

        // when
        redisRefreshTokenManager.storeTemporaryToken(refreshToken, id);

        // then
        Optional<Long> tokenOwnerId = redisTokenRepository.retrieveTemporaryRefreshToken(refreshToken);
        assertSoftly(softly -> {
            softly.assertThat(tokenOwnerId).isPresent();
            softly.assertThat(tokenOwnerId.get()).isEqualTo(2L);
        });
    }

    @DisplayName("RefreshToken 이 잘 조회되는지 확인한다.")
    @Test
    public void findTemporaryToken() {
        // given
        RefreshToken refreshToken = RefreshToken.of(UUID.randomUUID().toString());
        Long id = 1L;
        redisRefreshTokenManager.storeTemporaryToken(refreshToken, id);

        // when
        Optional<Long> temporaryToken = redisRefreshTokenManager.findTemporaryToken(refreshToken);

        // then
        assertThat(temporaryToken).isPresent();
        assertThat(temporaryToken.get()).isEqualTo(id);
    }

    @DisplayName("RefreshToken 이 삭제되는지 확인한다.")
    @Test
    public void removeTemporaryToken() {
        // given
        RefreshToken refreshToken = RefreshToken.of(UUID.randomUUID().toString());
        Long id = 1L;
        redisRefreshTokenManager.storeTemporaryToken(refreshToken, id);

        // when
        redisRefreshTokenManager.removeTemporaryToken(refreshToken);

        // then
        Optional<Long> temporaryToken = redisRefreshTokenManager.findTemporaryToken(refreshToken);
        assertThat(temporaryToken).isEmpty();
    }
}