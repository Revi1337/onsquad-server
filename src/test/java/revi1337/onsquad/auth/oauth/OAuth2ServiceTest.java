package revi1337.onsquad.auth.oauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import revi1337.onsquad.auth.oauth.config.OAuth2ClientProperties;
import revi1337.onsquad.auth.oauth.config.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.auth.oauth.config.SupportOAuth2Platform;
import revi1337.onsquad.auth.oauth.profile.user.GoogleUserProfile;
import revi1337.onsquad.auth.oauth.profile.user.KakaoUserProfile;
import revi1337.onsquad.auth.oauth.provider.PlatformOAuth2AccessTokenFetcher;
import revi1337.onsquad.auth.oauth.provider.PlatformOAuth2UserProfileFetcher;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.YamlPropertySourceFactory;
import revi1337.onsquad.common.config.system.properties.OnsquadProperties;
import revi1337.onsquad.token.domain.model.AccessToken;
import revi1337.onsquad.token.domain.model.JsonWebToken;

@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
@EnableConfigurationProperties({OAuth2ClientProperties.class, OnsquadProperties.class})
@ExtendWith(SpringExtension.class)
class OAuth2ServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private OAuth2ClientProperties oauth2ClientProperties;

    @MockBean
    private OAuth2LoginService oAuth2LoginService;

    @SpyBean
    private OAuth2Service oAuth2Service;

    @Nested
    @DisplayName("OAuth2 로그인 엔드포인트 생성을 테스트한다.")
    class BuildAuthorizationEndpoint {

        @Test
        @DisplayName("카카오 OAuth2 로그인 엔드포인트 생성에 성공한다.")
        void kakao() {
            String baseUrl = "http://localhost:8080";
            String platform = "kakao";
            OAuth2Properties oAuth2Properties = oauth2ClientProperties
                    .getPropertyFrom(SupportOAuth2Platform.KAKAO.getPlatform());

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
            OAuth2Properties oAuth2Properties = oauth2ClientProperties
                    .getPropertyFrom(SupportOAuth2Platform.GOOGLE.getPlatform());

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

    @Nested
    @DisplayName("OAuth2 로그인을 테스트한다.")
    class HandleOAuth2Login {

        @Test
        @DisplayName("카카오 OAuth2 로그인에 성공한다.")
        void kakao() {
            String baseUrl = "http://localhost:8080";
            String platform = "kakao";
            String authorizationCode = "authorization-code";
            OAuth2Platform kakaoPlatform = OAuth2Platform.KAKAO;
            forceChangeField(kakaoPlatform);
            PlatformOAuth2AccessTokenFetcher tokenFetcher = kakaoPlatform.getAccessTokenFetcher();
            PlatformOAuth2UserProfileFetcher profileFetcher = kakaoPlatform.getUserProfileFetcher();
            AccessToken mockAccessToken = mock(AccessToken.class);
            JsonWebToken mockJsonWebToken = mock(JsonWebToken.class);
            KakaoUserProfile mockKakaoUserProfile = mock(KakaoUserProfile.class);
            when(tokenFetcher.fetch(eq(baseUrl), eq(authorizationCode), any())).thenReturn(mockAccessToken);
            when(profileFetcher.fetch(eq(mockAccessToken), any())).thenReturn(mockKakaoUserProfile);
            when(oAuth2LoginService.loginOAuth2User(mockKakaoUserProfile)).thenReturn(mockJsonWebToken);

            URI uri = oAuth2Service.handleOAuth2Login(baseUrl, platform, authorizationCode);

            verify(oAuth2LoginService).loginOAuth2User(mockKakaoUserProfile);
            assertThat(uri).isNotNull();
        }

        @Test
        @DisplayName("구글 OAuth2 로그인에 성공한다.")
        void google() {
            String baseUrl = "http://localhost:8080";
            String platform = "google";
            String authorizationCode = "authorization-code";
            OAuth2Platform googlePlatform = OAuth2Platform.GOOGLE;
            forceChangeField(googlePlatform);
            PlatformOAuth2AccessTokenFetcher tokenFetcher = googlePlatform.getAccessTokenFetcher();
            PlatformOAuth2UserProfileFetcher profileFetcher = googlePlatform.getUserProfileFetcher();
            AccessToken mockAccessToken = mock(AccessToken.class);
            JsonWebToken mockJsonWebToken = mock(JsonWebToken.class);
            GoogleUserProfile mockGoogleUserProfile = mock(GoogleUserProfile.class);
            when(tokenFetcher.fetch(eq(baseUrl), eq(authorizationCode), any())).thenReturn(mockAccessToken);
            when(profileFetcher.fetch(eq(mockAccessToken), any())).thenReturn(mockGoogleUserProfile);
            when(oAuth2LoginService.loginOAuth2User(mockGoogleUserProfile)).thenReturn(mockJsonWebToken);

            URI uri = oAuth2Service.handleOAuth2Login(baseUrl, platform, authorizationCode);

            verify(oAuth2LoginService).loginOAuth2User(mockGoogleUserProfile);
            assertThat(uri).isNotNull();
        }

        private void forceChangeField(OAuth2Platform platform) {
            ReflectionTestUtils.setField(platform, "accessTokenFetcher",
                    mock(PlatformOAuth2AccessTokenFetcher.class));
            ReflectionTestUtils.setField(platform, "userProfileFetcher",
                    mock(PlatformOAuth2UserProfileFetcher.class));
        }
    }
}
