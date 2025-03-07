package revi1337.onsquad.auth.application.oauth2;

import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;

public class GoogleOAuth2EndpointBuilder implements PlatformOAuth2EndpointBuilder {

    public static final String WHITESPACE = " ";

    @Override
    public URI provideUsing(String baseUrl, OAuth2Properties oAuth2Properties) {
        return ServletUriComponentsBuilder
                .fromHttpUrl(oAuth2Properties.authorizationUri())
                .queryParam("client_id", oAuth2Properties.clientId())
                .queryParam("redirect_uri", baseUrl + oAuth2Properties.redirectUri())
                .queryParam("response_type", oAuth2Properties.responseType())
                .queryParam("scope", String.join(WHITESPACE, oAuth2Properties.scope().values()))
                .build()
                .toUri();
    }
}
