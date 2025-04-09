package revi1337.onsquad.auth.application.oauth;

import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
import revi1337.onsquad.auth.model.token.AccessToken;

public interface AuthorizationAccessTokenProvider {

    AccessToken provideAccessToken(String baseUrl, String authorizationCode,
                                   OAuth2ClientProperties oAuth2ClientProperties);

}
