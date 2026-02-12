package revi1337.onsquad.auth.oauth.application;

import revi1337.onsquad.auth.oauth.config.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.token.domain.model.AccessToken;

public interface PlatformOAuth2AccessTokenFetcher {

    AccessToken fetch(String baseUrl, String authorizationCode, OAuth2Properties oAuth2Properties);

}
