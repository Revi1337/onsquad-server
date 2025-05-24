package revi1337.onsquad.auth.application.oauth.provider.token;

import revi1337.onsquad.auth.application.token.model.AccessToken;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;

public interface PlatformOAuth2AccessTokenFetcher {

    AccessToken fetch(String baseUrl, String authorizationCode, OAuth2Properties oAuth2Properties);

}
