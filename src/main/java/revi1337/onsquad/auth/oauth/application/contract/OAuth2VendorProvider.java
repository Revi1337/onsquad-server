package revi1337.onsquad.auth.oauth.application.contract;

import revi1337.onsquad.auth.oauth.application.OAuth2Vendor;
import revi1337.onsquad.auth.oauth.infrastructure.OAuth2ClientProperties.OAuth2Properties;

public interface OAuth2VendorProvider {

    OAuth2Vendor platform();

    OAuth2Properties properties();

    OAuth2VendorEndpointBuilder endpointBuilder();

    OAuth2VendorAccessTokenFetcher accessTokenFetcher();

    OAuth2VendorUserProfileResolver userProfileResolver();

}
