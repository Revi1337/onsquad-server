package revi1337.onsquad.auth.application.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static revi1337.onsquad.common.fixture.TokenFixture.ACCESS_TOKEN_SUBJECT;
import static revi1337.onsquad.common.fixture.TokenFixture.REFRESH_TOKEN_SUBJECT;
import static revi1337.onsquad.common.fixture.TokenFixture.TEST_CLAIM_KEY_1;
import static revi1337.onsquad.common.fixture.TokenFixture.TEST_CLAIM_KEY_2;
import static revi1337.onsquad.common.fixture.TokenFixture.TEST_CLAIM_VALUE_1;
import static revi1337.onsquad.common.fixture.TokenFixture.TEST_CLAIM_VALUE_2;

import io.jsonwebtoken.Claims;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.auth.config.properties.TokenProperties;
import revi1337.onsquad.auth.config.properties.TokenProperties.AccessTokenAttributes;
import revi1337.onsquad.auth.config.properties.TokenProperties.RefreshTokenAttributes;
import revi1337.onsquad.auth.config.properties.TokenProperties.TokenAttributes;
import revi1337.onsquad.auth.error.exception.AuthTokenException;
import revi1337.onsquad.auth.model.token.AccessToken;
import revi1337.onsquad.auth.model.token.RefreshToken;
import revi1337.onsquad.common.YamlPropertySourceFactory;
import revi1337.onsquad.common.config.properties.OnsquadProperties;

@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
@EnableConfigurationProperties({TokenProperties.class, OnsquadProperties.class})
@ContextConfiguration(classes = {JsonWebTokenEvaluator.class, JsonWebTokenProvider.class})
@ExtendWith(SpringExtension.class)
class JsonWebTokenEvaluatorTest {

    @SpyBean
    private TokenProperties tokenProperties;

    @Autowired
    private JsonWebTokenEvaluator jsonWebTokenEvaluator;

    @Autowired
    private JsonWebTokenProvider jsonWebTokenProvider;

    @Nested
    @DisplayName("AccessToken 검증을 테스트한다.")
    class AccessTokenTest {

        @Test
        @DisplayName("AccessToken 검증에 성공한다.")
        void success() {
            AccessToken ACCESS_TOKEN = jsonWebTokenProvider
                    .generateAccessToken(ACCESS_TOKEN_SUBJECT, generatePayload());

            Claims claims = jsonWebTokenEvaluator.verifyAccessToken(ACCESS_TOKEN.value());

            assertThat(claims).isNotNull();
        }

        @Test
        @DisplayName("AccessToken Payload 의 subject 검증에 성공한다.")
        void success2() {
            AccessToken ACCESS_TOKEN = jsonWebTokenProvider
                    .generateAccessToken(ACCESS_TOKEN_SUBJECT, generatePayload());
            Claims CLAIMS = jsonWebTokenEvaluator.verifyAccessToken(ACCESS_TOKEN.value());

            String SUBJECT = CLAIMS.getSubject();

            assertThat(SUBJECT).isEqualTo(ACCESS_TOKEN_SUBJECT);
        }

        @Test
        @DisplayName("AccessToken Payload 의 기타 필드에 대한 검증에 성공한다.")
        void success3() {
            AccessToken ACCESS_TOKEN = jsonWebTokenProvider
                    .generateAccessToken(ACCESS_TOKEN_SUBJECT, generatePayload());
            Claims CLAIMS = jsonWebTokenEvaluator.verifyAccessToken(ACCESS_TOKEN.value());

            Object EXTRA_CLAIM = jsonWebTokenEvaluator
                    .extractSpecificClaim(CLAIMS, claims -> claims.get(TEST_CLAIM_KEY_1));

            assertThat(EXTRA_CLAIM).isEqualTo(TEST_CLAIM_VALUE_1);
        }

        @Test
        @DisplayName("Signature 가 다르면 AccessToken 검증에 실패한다.")
        void fail1() {
            AccessToken ACCESS_TOKEN = jsonWebTokenProvider
                    .generateAccessToken(ACCESS_TOKEN_SUBJECT, generatePayload());
            String INVALID_ACCESS_TOKEN = ACCESS_TOKEN.value() + "salt";

            assertThatThrownBy(() -> jsonWebTokenEvaluator.verifyAccessToken(INVALID_ACCESS_TOKEN))
                    .isExactlyInstanceOf(AuthTokenException.InvalidTokenSignature.class);
        }

        @Test
        @DisplayName("Expired Time 이 지나면 AccessToken 검증에 실패한다.")
        void fail2() {
            AccessTokenAttributes ACCESS_TOKEN_ATTRIBUTES = tokenProperties.accessTokenAttributes();
            TokenAttributes TOKEN_ATTRIBUTES = ACCESS_TOKEN_ATTRIBUTES.tokenAttributes();
            AccessTokenAttributes STUB_ATTRIBUTES = new AccessTokenAttributes(
                    new TokenAttributes(Duration.ofNanos(1), TOKEN_ATTRIBUTES.secretKey())
            );
            when(tokenProperties.accessTokenAttributes()).thenReturn(STUB_ATTRIBUTES);
            AccessToken ACCESS_TOKEN = jsonWebTokenProvider
                    .generateAccessToken(ACCESS_TOKEN_SUBJECT, generatePayload());

            assertThatThrownBy(() -> jsonWebTokenEvaluator.verifyAccessToken(ACCESS_TOKEN.value()))
                    .isExactlyInstanceOf(AuthTokenException.TokenExpired.class);
        }
    }

    @Nested
    @DisplayName("RefreshToken 검증을 테스트한다.")
    class RefreshTokenTest {

        @Test
        @DisplayName("RefreshToken 검증에 성공한다.")
        void success1() {
            RefreshToken REFRESH_TOKEN = jsonWebTokenProvider
                    .generateRefreshToken(REFRESH_TOKEN_SUBJECT, generatePayload());

            Claims claims = jsonWebTokenEvaluator.verifyRefreshToken(REFRESH_TOKEN.value());

            assertThat(claims).isNotNull();
        }

        @Test
        @DisplayName("RefreshToken Payload 의 subject 검증에 성공한다.")
        void success2() {
            RefreshToken REFRESH_TOKEN = jsonWebTokenProvider
                    .generateRefreshToken(REFRESH_TOKEN_SUBJECT, generatePayload());
            Claims claims = jsonWebTokenEvaluator.verifyRefreshToken(REFRESH_TOKEN.value());

            String SUBJECT = claims.getSubject();

            assertThat(SUBJECT).isEqualTo(REFRESH_TOKEN_SUBJECT);
        }

        @Test
        @DisplayName("RefreshToken Payload 의 기타 필드에 대한 검증에 성공한다.")
        void success3() {
            RefreshToken REFRESH_TOKEN = jsonWebTokenProvider
                    .generateRefreshToken(REFRESH_TOKEN_SUBJECT, generatePayload());
            Claims CLAIMS = jsonWebTokenEvaluator.verifyRefreshToken(REFRESH_TOKEN.value());

            Object EXTRA_CLAIM = jsonWebTokenEvaluator
                    .extractSpecificClaim(CLAIMS, claims -> claims.get(TEST_CLAIM_KEY_2));

            assertThat(EXTRA_CLAIM).isEqualTo(TEST_CLAIM_VALUE_2);
        }

        @Test
        @DisplayName("Signature 가 다르면 RefreshToken 검증에 실패한다.")
        void fail1() {
            RefreshToken REFRESH_TOKEN = jsonWebTokenProvider
                    .generateRefreshToken(REFRESH_TOKEN_SUBJECT, generatePayload());
            String INVALID_REFRESH_TOKEN = REFRESH_TOKEN.value() + "salt";

            assertThatThrownBy(() -> jsonWebTokenEvaluator.verifyRefreshToken(INVALID_REFRESH_TOKEN))
                    .isExactlyInstanceOf(AuthTokenException.InvalidTokenSignature.class);
        }

        @Test
        @DisplayName("Expired Time 이 지나면 RefreshToken 검증에 실패한다.")
        void fail2() {
            RefreshTokenAttributes REFRESH_TOKEN_ATTRIBUTES = tokenProperties.refreshTokenAttributes();
            TokenAttributes TOKEN_ATTRIBUTES = REFRESH_TOKEN_ATTRIBUTES.tokenAttributes();
            RefreshTokenAttributes STUB_ATTRIBUTES = new RefreshTokenAttributes(
                    new TokenAttributes(Duration.ofNanos(1), TOKEN_ATTRIBUTES.secretKey())
            );
            when(tokenProperties.refreshTokenAttributes()).thenReturn(STUB_ATTRIBUTES);
            RefreshToken REFRESH_TOKEN = jsonWebTokenProvider
                    .generateRefreshToken(REFRESH_TOKEN_SUBJECT, generatePayload());

            assertThatThrownBy(() -> jsonWebTokenEvaluator.verifyRefreshToken(REFRESH_TOKEN.value()))
                    .isExactlyInstanceOf(AuthTokenException.TokenExpired.class);
        }
    }

    private Map<String, String> generatePayload() {
        return new HashMap<>() {
            {
                put(TEST_CLAIM_KEY_1, TEST_CLAIM_VALUE_1);
                put(TEST_CLAIM_KEY_2, TEST_CLAIM_VALUE_2);
            }
        };
    }
}
