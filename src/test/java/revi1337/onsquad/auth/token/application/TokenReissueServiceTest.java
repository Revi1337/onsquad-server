package revi1337.onsquad.auth.token.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import revi1337.onsquad.auth.token.domain.model.JsonWebToken;
import revi1337.onsquad.auth.token.domain.model.RefreshToken;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;
import revi1337.onsquad.member.application.dto.MemberSummary;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@ContextConfiguration(initializers = RedisTestContainerInitializer.class)
class TokenReissueServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private TokenReissueService tokenReissueService;

    @Autowired
    private JsonWebTokenManager jsonWebTokenManager;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    @Qualifier("redisRefreshTokenStorage")
    private RefreshTokenStorage refreshTokenStorage;

    @BeforeEach
    void setUp() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    @Test
    @DisplayName("유효한 RefreshToken으로 재발급을 요청하면, 새로운 토큰 쌍을 반환하고 Redis의 기존 토큰을 갱신한다.")
    void reissueSuccess() {
        Member member = memberRepository.save(createRevi());
        Instant now = Instant.now();
        JsonWebToken oldTokenPair = jsonWebTokenManager.issueJsonWebToken(MemberSummary.from(member), now);

        JsonWebToken newTokenPair = tokenReissueService.reissue(oldTokenPair.refreshToken());

        assertSoftly(softly -> {
            softly.assertThat(newTokenPair).isNotNull();
            softly.assertThat(newTokenPair.accessToken()).isNotEqualTo(oldTokenPair.accessToken());
            softly.assertThat(newTokenPair.refreshToken()).isNotEqualTo(oldTokenPair.refreshToken());

            Optional<RefreshToken> savedToken = refreshTokenStorage.findTokenBy(member.getId());
            softly.assertThat(savedToken).isPresent();
            softly.assertThat(savedToken.get().value()).isEqualTo(newTokenPair.refreshToken());

            softly.assertThatThrownBy(() -> jsonWebTokenManager.verifyRefreshToken(oldTokenPair.refreshToken()));
        });
    }

    @Test
    @DisplayName("Redis에 저장된 토큰과 일치하지 않는 RefreshToken으로 재발급 요청 시 예외가 발생한다.")
    void reissueFailByMismatchedToken() {
        Member member = memberRepository.save(createRevi());
        jsonWebTokenManager.issueJsonWebToken(MemberSummary.from(member), Instant.now());
        String fakeToken = "fake.refresh.token";

        assertThatThrownBy(() -> tokenReissueService.reissue(fakeToken));
    }
}
