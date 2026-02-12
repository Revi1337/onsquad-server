package revi1337.onsquad.auth.oauth.application;

import revi1337.onsquad.auth.oauth.config.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.auth.oauth.domain.PlatformUserProfile;
import revi1337.onsquad.token.domain.model.AccessToken;

public interface PlatformOAuth2UserProfileFetcher {

    PlatformUserProfile fetch(AccessToken accessToken, OAuth2Properties oAuth2Properties);

}
