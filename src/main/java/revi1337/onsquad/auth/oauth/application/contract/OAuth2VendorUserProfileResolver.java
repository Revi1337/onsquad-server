package revi1337.onsquad.auth.oauth.application.contract;

import revi1337.onsquad.auth.oauth.infrastructure.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.token.domain.model.AccessToken;

public interface OAuth2VendorUserProfileResolver {

    OAuth2VendorUserProfile fetch(AccessToken accessToken, OAuth2Properties oAuth2Properties);

}
