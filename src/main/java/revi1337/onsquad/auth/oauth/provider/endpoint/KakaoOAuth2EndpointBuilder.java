package revi1337.onsquad.auth.oauth.provider.endpoint;

import java.net.URI;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import revi1337.onsquad.auth.oauth.config.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.auth.oauth.provider.PlatformOAuth2EndpointBuilder;

public class KakaoOAuth2EndpointBuilder implements PlatformOAuth2EndpointBuilder {

    @Override
    public URI build(String baseUrl, OAuth2Properties oAuth2Properties) {
        return ServletUriComponentsBuilder
                .fromHttpUrl(oAuth2Properties.authorizationUri())
                .queryParam("client_id", oAuth2Properties.clientId())
                .queryParam("redirect_uri", baseUrl + oAuth2Properties.redirectUri())
                .queryParam("response_type", oAuth2Properties.responseType())
                .build()
                .toUri();
    }
}
