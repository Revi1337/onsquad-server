package revi1337.onsquad.auth.application.oauth;

import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
import revi1337.onsquad.auth.model.oauth.PlatformUserProfile;
import revi1337.onsquad.auth.model.token.AccessToken;

public interface AuthorizationUserProfileProvider {

    PlatformUserProfile provideUserProfile(AccessToken accessToken, OAuth2ClientProperties oAuth2ClientProperties);

}
