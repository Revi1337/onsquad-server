package revi1337.onsquad.auth.application.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_USER_TYPE;
import static revi1337.onsquad.common.fixture.TokenFixture.ACCESS_TOKEN_SUBJECT;
import static revi1337.onsquad.common.fixture.TokenFixture.REFRESH_TOKEN_SUBJECT;

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
import revi1337.onsquad.member.application.dto.MemberSummary;

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
            AccessToken accessToken = jsonWebTokenProvider
                    .generateAccessToken(ACCESS_TOKEN_SUBJECT, generatePayload());

            ClaimsParser claimsParser = jsonWebTokenEvaluator.verifyAccessToken(accessToken.value());

            assertThat(claimsParser).isNotNull();
        }

        @Test
        @DisplayName("Signature 가 다르면 AccessToken 검증에 실패한다.")
        void fail1() {
            AccessToken accessToken = jsonWebTokenProvider.generateAccessToken(ACCESS_TOKEN_SUBJECT, generatePayload());
            String invalidAccessToken = accessToken.value() + "salt";

            assertThatThrownBy(() -> jsonWebTokenEvaluator.verifyAccessToken(invalidAccessToken))
                    .isExactlyInstanceOf(AuthTokenException.InvalidTokenSignature.class);
        }

        @Test
        @DisplayName("Expired Time 이 지나면 AccessToken 검증에 실패한다.")
        void fail2() {
            AccessTokenAttributes accessTokenAttributes = tokenProperties.accessTokenAttributes();
            TokenAttributes tokenAttributes = accessTokenAttributes.tokenAttributes();
            AccessTokenAttributes stubAttributes = new AccessTokenAttributes(
                    new TokenAttributes(Duration.ofNanos(1), tokenAttributes.secretKey())
            );
            when(tokenProperties.accessTokenAttributes()).thenReturn(stubAttributes);
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
            RefreshToken refreshToken = jsonWebTokenProvider
                    .generateRefreshToken(REFRESH_TOKEN_SUBJECT, generatePayload());

            ClaimsParser claimsParser = jsonWebTokenEvaluator.verifyRefreshToken(refreshToken.value());

            assertThat(claimsParser).isNotNull();
        }

        @Test
        @DisplayName("Signature 가 다르면 RefreshToken 검증에 실패한다.")
        void fail1() {
            RefreshToken refreshToken = jsonWebTokenProvider
                    .generateRefreshToken(REFRESH_TOKEN_SUBJECT, generatePayload());
            String invalidRefreshToken = refreshToken.value() + "salt";

            assertThatThrownBy(() -> jsonWebTokenEvaluator.verifyRefreshToken(invalidRefreshToken))
                    .isExactlyInstanceOf(AuthTokenException.InvalidTokenSignature.class);
        }

        @Test
        @DisplayName("Expired Time 이 지나면 RefreshToken 검증에 실패한다.")
        void fail2() {
            RefreshTokenAttributes refreshTokenAttributes = tokenProperties.refreshTokenAttributes();
            TokenAttributes tokenAttributes = refreshTokenAttributes.tokenAttributes();
            RefreshTokenAttributes stubAttributes = new RefreshTokenAttributes(
                    new TokenAttributes(Duration.ofNanos(1), tokenAttributes.secretKey())
            );
            when(tokenProperties.refreshTokenAttributes()).thenReturn(stubAttributes);
            RefreshToken refreshToken = jsonWebTokenProvider
                    .generateRefreshToken(REFRESH_TOKEN_SUBJECT, generatePayload());

            assertThatThrownBy(() -> jsonWebTokenEvaluator.verifyRefreshToken(refreshToken.value()))
                    .isExactlyInstanceOf(AuthTokenException.TokenExpired.class);
        }
    }

    private Map<String, Object> generatePayload() {
        MemberSummary summary = new MemberSummary(1L, REVI_EMAIL_VALUE, null, REVI_USER_TYPE);
        return new HashMap<>() {
            {
                put(ClaimsParser.IDENTITY_CLAIM, summary.id());
                put(ClaimsParser.EMAIL_CLAIM, summary.email());
                put(ClaimsParser.USERTYPE_CLAIM, summary.userType().name());
            }
        };
    }
}
