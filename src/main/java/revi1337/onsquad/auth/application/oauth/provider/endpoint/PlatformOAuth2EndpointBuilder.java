package revi1337.onsquad.auth.application.oauth.provider.endpoint;

import java.net.URI;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;

public interface PlatformOAuth2EndpointBuilder {

    URI provideUsing(String baseUrl, OAuth2Properties oAuth2Properties);

}
