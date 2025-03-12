package revi1337.onsquad.auth.application.oauth2;

import revi1337.onsquad.auth.application.token.AccessToken;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;

public interface PlatformOAuth2AccessTokenFetcher {

    AccessToken fetchAccessToken(String baseUrl, String authorizationCode, OAuth2Properties oAuth2Properties);

}
