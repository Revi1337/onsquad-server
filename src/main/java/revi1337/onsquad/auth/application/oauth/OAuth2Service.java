package revi1337.onsquad.auth.application.oauth;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import revi1337.onsquad.auth.application.oauth.model.PlatformUserProfile;
import revi1337.onsquad.auth.application.token.model.AccessToken;
import revi1337.onsquad.auth.application.token.model.JsonWebToken;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
import revi1337.onsquad.auth.config.properties.SupportOAuth2Platform;
import revi1337.onsquad.common.config.properties.OnsquadProperties;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2Service {

    private static final String ACCESS_TOKEN_PARAMETER = "accessToken";
    private static final String REFRESH_TOKEN_PARAMETER = "refreshToken";

    private final OnsquadProperties onsquadProperties;
    private final OAuth2ClientProperties oAuth2ClientProperties;
    private final OAuth2LoginService loginService;

    public URI buildAuthorizationEndpoint(String baseUrl, String platform) {
        OAuth2Platform oAuth2Platform = retreiveAvailableOAuth2Platform(platform);
        return oAuth2Platform.provideUsing(baseUrl, oAuth2ClientProperties);
    }

    public URI handleOAuth2Login(String baseUrl, String platform, String code) {
        PlatformUserProfile userProfile = extractUserProfile(baseUrl, platform, code);
        JsonWebToken jsonWebToken = loginService.loginOAuth2User(userProfile);

        return buildRedirectUri(jsonWebToken);
    }

    private PlatformUserProfile extractUserProfile(String baseUrl, String platform, String authorizationCode) {
        OAuth2Platform oAuth2Platform = retreiveAvailableOAuth2Platform(platform);
        AccessToken accessToken = oAuth2Platform.provideAccessToken(baseUrl, authorizationCode, oAuth2ClientProperties);
        return oAuth2Platform.provideUserProfile(accessToken, oAuth2ClientProperties);
    }

    private URI buildRedirectUri(JsonWebToken jsonWebToken) {
        return ServletUriComponentsBuilder.fromHttpUrl(onsquadProperties.getFrontendBaseUrl())
                .queryParam(ACCESS_TOKEN_PARAMETER, jsonWebToken.accessToken())
                .queryParam(REFRESH_TOKEN_PARAMETER, jsonWebToken.refreshToken())
                .build()
                .toUri();
    }

    private OAuth2Platform retreiveAvailableOAuth2Platform(String platform) {
        return SupportOAuth2Platform.getAvailableFromSpecific(platform);
    }
}
