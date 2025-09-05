package revi1337.onsquad.auth.application.token;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_USER_TYPE;

import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import revi1337.onsquad.auth.application.token.model.AccessToken;
import revi1337.onsquad.auth.application.token.model.RefreshToken;
import revi1337.onsquad.auth.repository.token.ExpiringMapTokenRepository;
import revi1337.onsquad.auth.repository.token.RedisHashTokenRepository;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.ApplicationLayerWithTestContainerSupport;
import revi1337.onsquad.member.application.dto.MemberSummary;

class JsonWebTokenManagerTest {

    @Nested
    @DisplayName("Redis를 사용한 JsonWebTokenManagerTest")
    class RedisJsonWebTokenManagerTest extends ApplicationLayerWithTestContainerSupport {

        private final RedisHashTokenRepository redisHashTokenRepository;
        private final JsonWebTokenManager jsonWebTokenManager;

        @Autowired
        private RedisJsonWebTokenManagerTest(
                RedisHashTokenRepository redisHashTokenRepository,
                JsonWebTokenProvider jsonWebTokenProvider,
                RedisRefreshTokenManager refreshTokenManager
        ) {
            this.redisHashTokenRepository = redisHashTokenRepository;
            this.jsonWebTokenManager = new JsonWebTokenManager(jsonWebTokenProvider, refreshTokenManager);
        }

        @BeforeEach
        void setUp() {
            redisHashTokenRepository.deleteAll();
        }

        @Test
        @DisplayName("AccessToken 생성에 성공한다.")
        void generateAccessToken() {
            MemberSummary SUMMARY = new MemberSummary(1L, REVI_EMAIL_VALUE, null, REVI_USER_TYPE);

            AccessToken ACCESS_TOKEN = jsonWebTokenManager.generateAccessToken(SUMMARY);

            assertThat(ACCESS_TOKEN).isNotNull();
        }

        @Test
        @DisplayName("RefreshToken 생성에 성공한다.")
        void generateRefreshToken() {
            Long MEMBER_ID = 1L;

            RefreshToken REFRESH_TOKEN = jsonWebTokenManager.generateRefreshToken(MEMBER_ID);

            assertThat(REFRESH_TOKEN).isNotNull();
        }

        @Test
        @DisplayName("RefreshToken 저장에 성공한다.")
        void storeRefreshTokenFor() {
            Long MEMBER_ID = 1L;
            RefreshToken REFRESH_TOKEN = jsonWebTokenManager.generateRefreshToken(MEMBER_ID);

            jsonWebTokenManager.storeRefreshTokenFor(MEMBER_ID, REFRESH_TOKEN);

            assertThat(redisHashTokenRepository.findBy(MEMBER_ID)).isPresent();
        }

        @Test
        @DisplayName("RefreshToken 조회에 성공한다.")
        void findRefreshTokenBy() {
            Long MEMBER_ID = 1L;
            RefreshToken REFRESH_TOKEN = jsonWebTokenManager.generateRefreshToken(MEMBER_ID);
            redisHashTokenRepository.save(REFRESH_TOKEN, MEMBER_ID, Duration.ofSeconds(5));

            Optional<RefreshToken> FIND_TOKEN = jsonWebTokenManager.findRefreshTokenBy(MEMBER_ID);

            assertThat(FIND_TOKEN).isPresent();
        }
    }

    @Nested
    @DisplayName("ExpiringMap을 사용한 JsonWebTokenManagerTest")
    class ExpiringMapJsonWebTokenManagerTest extends ApplicationLayerTestSupport {

        private final ExpiringMapTokenRepository expiringMapTokenRepository;
        private final JsonWebTokenManager jsonWebTokenManager;

        @Autowired
        private ExpiringMapJsonWebTokenManagerTest(
                ExpiringMapTokenRepository expiringMapTokenRepository,
                JsonWebTokenProvider jsonWebTokenProvider,
                ExpiringMapRefreshTokenManager refreshTokenManager
        ) {
            this.expiringMapTokenRepository = expiringMapTokenRepository;
            this.jsonWebTokenManager = new JsonWebTokenManager(jsonWebTokenProvider, refreshTokenManager);
        }

        @BeforeEach
        void setUp() {
            expiringMapTokenRepository.deleteAll();
        }

        @Test
        @DisplayName("AccessToken 생성에 성공한다.")
        void generateAccessToken() {
            MemberSummary SUMMARY = new MemberSummary(1L, REVI_EMAIL_VALUE, null, REVI_USER_TYPE);

            AccessToken ACCESS_TOKEN = jsonWebTokenManager.generateAccessToken(SUMMARY);

            assertThat(ACCESS_TOKEN).isNotNull();
        }

        @Test
        @DisplayName("RefreshToken 생성에 성공한다.")
        void generateRefreshToken() {
            Long MEMBER_ID = 1L;

            RefreshToken REFRESH_TOKEN = jsonWebTokenManager.generateRefreshToken(MEMBER_ID);

            assertThat(REFRESH_TOKEN).isNotNull();
        }

        @Test
        @DisplayName("RefreshToken 저장에 성공한다.")
        void storeRefreshTokenFor() {
            Long MEMBER_ID = 1L;
            RefreshToken REFRESH_TOKEN = jsonWebTokenManager.generateRefreshToken(MEMBER_ID);

            jsonWebTokenManager.storeRefreshTokenFor(MEMBER_ID, REFRESH_TOKEN);

            assertThat(expiringMapTokenRepository.findBy(MEMBER_ID)).isPresent();
        }

        @Test
        @DisplayName("RefreshToken 조회에 성공한다.")
        void findRefreshTokenBy() {
            Long MEMBER_ID = 1L;
            RefreshToken REFRESH_TOKEN = jsonWebTokenManager.generateRefreshToken(MEMBER_ID);
            expiringMapTokenRepository.save(REFRESH_TOKEN, MEMBER_ID, Duration.ofSeconds(5));

            Optional<RefreshToken> FIND_TOKEN = jsonWebTokenManager.findRefreshTokenBy(MEMBER_ID);

            assertThat(FIND_TOKEN).isPresent();
        }
    }
}
