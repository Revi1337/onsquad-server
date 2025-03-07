package revi1337.onsquad.auth.application.oauth2;

import java.net.URI;
import revi1337.onsquad.auth.application.oauth2.model.PlatformUserProfile;
import revi1337.onsquad.auth.application.token.AccessToken;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.auth.config.properties.SupportOAuth2Platform;

public enum OAuth2Platform implements AuthorizationEndPointProvider, AuthorizationAccessTokenProvider,
        AuthorizationUserProfileProvider {

    KAKAO(
            new KakaoOAuth2EndpointBuilder(),
            new KakaoOAuth2AccessTokenEvaluator(),
            new KakaoOAuth2UserProfileEvaluator()
    ),
    GOOGLE(
            new GoogleOAuth2EndpointBuilder(),
            new GoogleOAuth2AccessTokenEvaluator(),
            new GoogleOAuth2UserProfileEvaluator()
    );

    private final PlatformOAuth2EndpointBuilder endpointBuilder;
    private final PlatformOAuth2AccessTokenEvaluator tokenEvaluator;
    private final PlatformOAuth2UserProfileEvaluator userProfileEvaluator;

    OAuth2Platform(PlatformOAuth2EndpointBuilder endpointBuilder,
                   PlatformOAuth2AccessTokenEvaluator tokenEvaluator,
                   PlatformOAuth2UserProfileEvaluator userProfileEvaluator) {
        this.endpointBuilder = endpointBuilder;
        this.tokenEvaluator = tokenEvaluator;
        this.userProfileEvaluator = userProfileEvaluator;
    }

    @Override
    public AccessToken provideAccessToken(String baseUrl, String authorizationCode,
                                          OAuth2ClientProperties oAuth2ClientProperties) {
        OAuth2Properties oAuth2Properties = getPropertyFrom(oAuth2ClientProperties);
        return tokenEvaluator.provideAccessToken(baseUrl, authorizationCode, oAuth2Properties);
    }

    @Override
    public URI provideUsing(String baseUrl, OAuth2ClientProperties oAuth2ClientProperties) {
        OAuth2Properties oAuth2Properties = getPropertyFrom(oAuth2ClientProperties);
        return endpointBuilder.provideUsing(baseUrl, oAuth2Properties);
    }

    @Override
    public PlatformUserProfile provideUserProfile(AccessToken accessToken,
                                                  OAuth2ClientProperties oAuth2ClientProperties) {
        OAuth2Properties oAuth2Properties = getPropertyFrom(oAuth2ClientProperties);
        return userProfileEvaluator.provideUserProfile(accessToken, oAuth2Properties);
    }

    public OAuth2Properties getPropertyFrom(OAuth2ClientProperties oAuth2ClientProperties) {
        SupportOAuth2Platform platformKey = SupportOAuth2Platform.valueOf(this.name());
        return oAuth2ClientProperties.clients().get(platformKey);
    }
}
