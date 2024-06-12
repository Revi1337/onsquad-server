package revi1337.onsquad.auth.application;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import revi1337.onsquad.auth.dto.response.AccessToken;
import revi1337.onsquad.auth.error.exception.TokenExpiredException;
import revi1337.onsquad.config.PropertiesConfiguration;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@PropertySource("classpath:application-token.yml")
@DisplayName("JsonWebTokenEvaluator 테스트")
@Import({JsonWebTokenProvider.class, JsonWebTokenEvaluator.class})
class JsonWebTokenEvaluatorTest extends PropertiesConfiguration {

    @Autowired private JsonWebTokenProvider jsonWebTokenProvider;
    @Autowired private JsonWebTokenEvaluator jsonWebTokenEvaluator;

    @Test
    @DisplayName("AccessToken 이 잘 검증되는지 확인한다")
    public void verifyAccessToken() {
        // given
        String subject = "revi1337";
        Map<String, Integer> identifier = Collections.singletonMap("identifier", 1);
        AccessToken accessToken = jsonWebTokenProvider.generateAccessToken(subject, identifier);

        // when
        Claims jsonWebTokenClaims = jsonWebTokenEvaluator.verifyAccessToken(accessToken.value());

        // then
        assertThat(jsonWebTokenClaims.getSubject()).isNotNull();
    }

    @Test
    @DisplayName("AccessToken 이 만료되면 실패한다.")
    public void verifyAccessToken2() throws InterruptedException {
        String subject = "revi1337";
        Map<String, Integer> identifier = Collections.singletonMap("identifier", 1);
        AccessToken accessToken = jsonWebTokenProvider.generateAccessToken(subject, identifier);

        // when && then
        Thread.sleep(1100);
        assertThatThrownBy(() -> jsonWebTokenEvaluator.verifyAccessToken(accessToken.value()))
                .isInstanceOf(TokenExpiredException.class)
                .hasMessage("토큰 만료날짜가 지났음");
    }
}