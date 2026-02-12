package revi1337.onsquad.auth.oauth.infrastructure.google;

import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import revi1337.onsquad.auth.oauth.application.PlatformOAuth2EndpointBuilder;
import revi1337.onsquad.auth.oauth.config.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.common.constant.Sign;

public class GoogleOAuth2EndpointBuilder implements PlatformOAuth2EndpointBuilder {

    @Override
    public URI build(String baseUrl, OAuth2Properties oAuth2Properties) {
        return ServletUriComponentsBuilder
                .fromHttpUrl(oAuth2Properties.authorizationUri())
                .queryParam("client_id", oAuth2Properties.clientId())
                .queryParam("redirect_uri", baseUrl + oAuth2Properties.redirectUri())
                .queryParam("response_type", oAuth2Properties.responseType())
                .queryParam("scope", String.join(Sign.WHITESPACE, oAuth2Properties.scope().values()))
                .build()
                .toUri();
    }
}
