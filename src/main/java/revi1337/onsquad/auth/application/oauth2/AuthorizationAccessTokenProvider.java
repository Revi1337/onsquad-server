package revi1337.onsquad.auth.application.oauth2;

import revi1337.onsquad.auth.application.token.AccessToken;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;

public interface AuthorizationAccessTokenProvider {

    AccessToken provideAccessToken(String baseUrl, String authorizationCode,
                                   OAuth2ClientProperties oAuth2ClientProperties);

}
