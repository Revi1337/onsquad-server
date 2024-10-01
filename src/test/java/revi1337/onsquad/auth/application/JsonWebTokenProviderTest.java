package revi1337.onsquad.auth.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.auth.application.token.AccessToken;
import revi1337.onsquad.auth.application.token.RefreshToken;
import revi1337.onsquad.config.PropertiesConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JsonWebTokenProvider 테스트")
@Import(JsonWebTokenProvider.class)
public class JsonWebTokenProviderTest extends PropertiesConfiguration {

    @Autowired private JsonWebTokenProvider jsonWebTokenProvider;

    @DisplayName("AccessToken 이 잘 만들어지는지 확인한다")
    @Test
    public void generateAccessToken() {
        // given
        String subject = "revi1337";
        Map<String, Integer> memberId = Collections.singletonMap("memberId", 1);

        // when
        AccessToken accessToken = jsonWebTokenProvider.generateAccessToken(subject, memberId);

        // then
        assertThat(Arrays.stream(accessToken.value().split("\\.")).count()).isEqualTo(3);
    }

    @DisplayName("RefreshToken 이 잘 만들어지는지 확인한다.")
    @Test
    public void generateRefreshToken() {
        // given
        String subject = "revi1337";
        Map<String, Integer> identifier = Collections.singletonMap("memberId", 1);

        // when
        RefreshToken refreshToken = jsonWebTokenProvider.generateRefreshToken(subject, identifier);

        // then
        assertThat(Arrays.stream(refreshToken.value().split("\\.")).count()).isEqualTo(3);
    }
}
