package revi1337.onsquad.auth.application.oauth;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import revi1337.onsquad.auth.application.oauth.model.PlatformUserProfile;
import revi1337.onsquad.auth.application.oauth.provider.endpoint.PlatformOAuth2EndpointBuilder;
import revi1337.onsquad.auth.application.oauth.provider.token.PlatformOAuth2AccessTokenFetcher;
import revi1337.onsquad.auth.application.oauth.provider.user.PlatformOAuth2UserProfileFetcher;
import revi1337.onsquad.auth.application.token.model.AccessToken;
import revi1337.onsquad.auth.application.token.model.JsonWebToken;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.auth.config.properties.SupportOAuth2Platform;
import revi1337.onsquad.common.config.properties.OnsquadProperties;

@RequiredArgsConstructor
@Service
public class OAuth2Service {

    private static final String ACCESS_TOKEN_PARAMETER = "accessToken";
    private static final String REFRESH_TOKEN_PARAMETER = "refreshToken";

    private final OnsquadProperties onsquadProperties;
    private final OAuth2ClientProperties oAuth2ClientProperties;
    private final OAuth2LoginService loginService;

    public URI buildAuthorizationEndpoint(String baseUrl, String platform) {
        OAuth2Platform oAuth2Platform = SupportOAuth2Platform.getAvailableFrom(platform);
        PlatformOAuth2EndpointBuilder endpointBuilder = oAuth2Platform.getEndpointBuilder();

        OAuth2Properties oAuth2Properties = oAuth2ClientProperties.getPropertyFrom(oAuth2Platform);

        return endpointBuilder.build(baseUrl, oAuth2Properties);
    }

    public URI handleOAuth2Login(String baseUrl, String platform, String code) {
        PlatformUserProfile userProfile = fetchUserProfile(baseUrl, platform, code);
        JsonWebToken jsonWebToken = loginService.loginOAuth2User(userProfile);

        return buildRedirectUri(jsonWebToken);
    }

    private PlatformUserProfile fetchUserProfile(String baseUrl, String platform, String authorizationCode) {
        OAuth2Platform oAuth2Platform = SupportOAuth2Platform.getAvailableFrom(platform);
        PlatformOAuth2AccessTokenFetcher tokenEvaluator = oAuth2Platform.getAccessTokenFetcher();
        PlatformOAuth2UserProfileFetcher userProfileEvaluator = oAuth2Platform.getUserProfileFetcher();

        OAuth2Properties oAuth2Properties = oAuth2ClientProperties.getPropertyFrom(oAuth2Platform);

        AccessToken accessToken = tokenEvaluator.fetch(baseUrl, authorizationCode, oAuth2Properties);
        return userProfileEvaluator.fetch(accessToken, oAuth2Properties);
    }

    private URI buildRedirectUri(JsonWebToken jsonWebToken) {
        return UriComponentsBuilder.fromHttpUrl(onsquadProperties.getFrontendBaseUrl())
                .queryParam(ACCESS_TOKEN_PARAMETER, jsonWebToken.accessToken())
                .queryParam(REFRESH_TOKEN_PARAMETER, jsonWebToken.refreshToken())
                .build()
                .toUri();
    }
}
