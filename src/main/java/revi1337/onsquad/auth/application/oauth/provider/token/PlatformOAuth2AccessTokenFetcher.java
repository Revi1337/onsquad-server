package revi1337.onsquad.auth.application.oauth.provider.token;

import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.auth.model.token.AccessToken;

public interface PlatformOAuth2AccessTokenFetcher {

    AccessToken fetchAccessToken(String baseUrl, String authorizationCode, OAuth2Properties oAuth2Properties);

}
