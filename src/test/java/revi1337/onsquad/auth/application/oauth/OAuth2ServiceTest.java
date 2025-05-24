package revi1337.onsquad.auth.application.oauth;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.auth.config.properties.SupportOAuth2Platform;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.YamlPropertySourceFactory;
import revi1337.onsquad.common.config.properties.OnsquadProperties;

@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
@EnableConfigurationProperties({OAuth2ClientProperties.class, OnsquadProperties.class})
@ExtendWith(SpringExtension.class)
class OAuth2ServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private OAuth2ClientProperties oauth2ClientProperties;

    @Autowired
    private OAuth2Service oAuth2Service;

    @Nested
    @DisplayName("OAuth2 로그인 엔드포인트 생성을 테스트한다.")
    class BuildAuthorizationEndpoint {

        @Test
        @DisplayName("카카오 OAuth2 로그인 엔드포인트 생성에 성공한다.")
        void kakao() {
            String baseUrl = "http://localhost:8080";
            String platform = "kakao";
            OAuth2Properties oAuth2Properties = oauth2ClientProperties.clients().get(SupportOAuth2Platform.KAKAO);
            String endpointQuery = String.join("&",
                    String.format("client_id=%s", oAuth2Properties.clientId()),
                    String.format("redirect_uri=%s", baseUrl + oAuth2Properties.redirectUri()),
                    String.format("response_type=%s", oAuth2Properties.responseType())
            );

            URI uri = oAuth2Service.buildAuthorizationEndpoint(baseUrl, platform);

            assertThat(uri.getQuery()).isEqualTo(endpointQuery);
        }

        @Test
        @DisplayName("구글 OAuth2 로그인 엔드포인트 생성에 성공한다.")
        void google() {
            String baseUrl = "http://localhost:8080";
            String platform = "google";
            OAuth2Properties oAuth2Properties = oauth2ClientProperties.clients().get(SupportOAuth2Platform.GOOGLE);

            String endpointQuery = String.join("&",
                    String.format("client_id=%s", oAuth2Properties.clientId()),
                    String.format("redirect_uri=%s", baseUrl + oAuth2Properties.redirectUri()),
                    String.format("response_type=%s", oAuth2Properties.responseType()),
                    String.format("scope=%s", String.join(" ", oAuth2Properties.scope().values()))
            );

            URI uri = oAuth2Service.buildAuthorizationEndpoint(baseUrl, platform);

            assertThat(uri.getQuery()).isEqualTo(endpointQuery);
        }
    }
}
