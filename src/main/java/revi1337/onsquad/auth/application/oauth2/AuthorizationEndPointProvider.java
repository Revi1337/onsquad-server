package revi1337.onsquad.auth.application.oauth2;

import java.net.URI;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;

public interface AuthorizationEndPointProvider {

    URI provideUsing(String baseUrl, OAuth2ClientProperties oAuth2ClientProperties);

}
