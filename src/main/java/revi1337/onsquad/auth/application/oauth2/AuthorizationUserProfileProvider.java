package revi1337.onsquad.auth.application.oauth2;

import revi1337.onsquad.auth.application.oauth2.model.PlatformUserProfile;
import revi1337.onsquad.auth.application.token.AccessToken;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;

public interface AuthorizationUserProfileProvider {

    PlatformUserProfile provideUserProfile(String baseUrl, AccessToken accessToken,
                                           OAuth2ClientProperties oAuth2ClientProperties);

}
