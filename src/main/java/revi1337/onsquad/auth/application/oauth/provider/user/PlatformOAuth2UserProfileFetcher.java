package revi1337.onsquad.auth.application.oauth.provider.user;

import revi1337.onsquad.auth.application.oauth.model.PlatformUserProfile;
import revi1337.onsquad.auth.application.token.model.AccessToken;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;

public interface PlatformOAuth2UserProfileFetcher {

    PlatformUserProfile fetch(AccessToken accessToken, OAuth2Properties oAuth2Properties);

}
