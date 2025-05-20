package revi1337.onsquad.auth.application.oauth;

import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
import revi1337.onsquad.auth.application.oauth.model.PlatformUserProfile;
import revi1337.onsquad.auth.application.token.model.AccessToken;

public interface AuthorizationUserProfileProvider {

    PlatformUserProfile provideUserProfile(AccessToken accessToken, OAuth2ClientProperties oAuth2ClientProperties);

}
