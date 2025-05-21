package revi1337.onsquad.auth.application.token;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.TokenFixture.ACCESS_TOKEN_SUBJECT;
import static revi1337.onsquad.common.fixture.TokenFixture.CLAIMS;
import static revi1337.onsquad.common.fixture.TokenFixture.REFRESH_TOKEN_SUBJECT;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import revi1337.onsquad.auth.application.token.model.AccessToken;
import revi1337.onsquad.auth.application.token.model.RefreshToken;
import revi1337.onsquad.auth.config.properties.TokenProperties;
import revi1337.onsquad.common.YamlPropertySourceFactory;
import revi1337.onsquad.common.config.properties.OnsquadProperties;

@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
@EnableConfigurationProperties({TokenProperties.class, OnsquadProperties.class})
@ContextConfiguration(classes = JsonWebTokenProvider.class)
@ExtendWith(SpringExtension.class)
class JsonWebTokenProviderTest {

    @Autowired
    private JsonWebTokenProvider jsonWebTokenProvider;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("AccessToken 생성에 성공한다.")
    void success1() throws IOException {
        // given
        HashMap<String, String> TOKEN_CLAIMS = CLAIMS;

        // when
        AccessToken ACCESS_TOKEN = jsonWebTokenProvider.generateAccessToken(ACCESS_TOKEN_SUBJECT, TOKEN_CLAIMS);

        // then
        assertThat(ACCESS_TOKEN).isNotNull();
        assertThat(parseAccessTokenPayload(ACCESS_TOKEN).get("sub")).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }

    @Test
    @DisplayName("RefreshToken 생성에 성공한다.")
    void success2() throws IOException {
        // given
        HashMap<String, String> TOKEN_CLAIMS = CLAIMS;

        // when
        RefreshToken REFRESH_TOKEN = jsonWebTokenProvider.generateRefreshToken(REFRESH_TOKEN_SUBJECT, TOKEN_CLAIMS);

        // then
        assertThat(REFRESH_TOKEN).isNotNull();
        assertThat(parseRefreshTokenPayload(REFRESH_TOKEN).get("sub")).isEqualTo(REFRESH_TOKEN_SUBJECT);
    }

    private Map<String, String> parseAccessTokenPayload(AccessToken accessToken) throws IOException {
        String PAYLOAD = new String(Base64.getUrlDecoder().decode(accessToken.value().split("\\.")[1]));
        return objectMapper.readValue(
                PAYLOAD,
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class)
        );
    }

    private Map<String, String> parseRefreshTokenPayload(RefreshToken refreshToken) throws IOException {
        String PAYLOAD = new String(Base64.getUrlDecoder().decode(refreshToken.value().split("\\.")[1]));
        return objectMapper.readValue(
                PAYLOAD,
                objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class)
        );
    }
}