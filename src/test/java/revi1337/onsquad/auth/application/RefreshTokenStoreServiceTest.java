package revi1337.onsquad.auth.application;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import revi1337.onsquad.auth.domain.redis.RedisTokenRepository;
import revi1337.onsquad.auth.dto.response.RefreshToken;
import revi1337.onsquad.support.TestContainerSupport;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TODO TokenProperties 를 Mocking 하여 Stubbing 해야하는데... 아무리 찾아봐도 방법이 나오지않는다.
 *  Reflection 을 사용해서 할 수 있다고는하는데.. 시간관계상 슬라이스테스트 대신 SpringBootTest 로 대체한다.
 */
@SpringBootTest
class RefreshTokenStoreServiceTest extends TestContainerSupport {

    @Autowired private StringRedisTemplate stringRedisTemplate;
    @Autowired private RedisTokenRepository redisTokenRepository;
    @Autowired private RefreshTokenStoreService refreshTokenStoreService;

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
        refreshTokenStoreService.storeTemporaryToken(refreshToken, id);

        // then
        String memberId = redisTokenRepository.retrieveTemporaryRefreshToken(refreshToken);
        assertThat(memberId).isEqualTo(id.toString());
    }
}