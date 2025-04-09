package revi1337.onsquad.auth.application.oauth.provider.user;

import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.auth.model.oauth.PlatformUserProfile;
import revi1337.onsquad.auth.model.token.AccessToken;

public interface PlatformOAuth2UserProfileFetcher {

    PlatformUserProfile fetchUserProfile(AccessToken accessToken, OAuth2Properties oAuth2Properties);

}
