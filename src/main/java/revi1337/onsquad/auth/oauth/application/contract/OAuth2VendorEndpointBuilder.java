package revi1337.onsquad.auth.oauth.application.contract;

import java.net.URI;
import revi1337.onsquad.auth.oauth.infrastructure.OAuth2ClientProperties.OAuth2Properties;

public interface OAuth2VendorEndpointBuilder {

    URI build(String baseUrl, OAuth2Properties oAuth2Properties);

}
