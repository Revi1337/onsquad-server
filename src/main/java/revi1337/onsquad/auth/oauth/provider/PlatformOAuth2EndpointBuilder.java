package revi1337.onsquad.auth.oauth.provider;

import java.net.URI;
import revi1337.onsquad.auth.oauth.config.OAuth2ClientProperties.OAuth2Properties;

public interface PlatformOAuth2EndpointBuilder {

    URI build(String baseUrl, OAuth2Properties oAuth2Properties);

}
