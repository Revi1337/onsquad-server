package revi1337.onsquad.auth.oauth.infrastructure.kakao;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.auth.oauth.application.OAuth2Vendor;
import revi1337.onsquad.auth.oauth.application.contract.OAuth2VendorAccessTokenFetcher;
import revi1337.onsquad.auth.oauth.application.contract.OAuth2VendorEndpointBuilder;
import revi1337.onsquad.auth.oauth.application.contract.OAuth2VendorProvider;
import revi1337.onsquad.auth.oauth.application.contract.OAuth2VendorUserProfileResolver;
import revi1337.onsquad.auth.oauth.infrastructure.OAuth2ClientProperties;
import revi1337.onsquad.auth.oauth.infrastructure.OAuth2ClientProperties.OAuth2Properties;

@Component
@RequiredArgsConstructor
public class KakaoOAuth2Provider implements OAuth2VendorProvider {

    private final OAuth2ClientProperties clientProperties;

    @Override
    public OAuth2Vendor platform() {
        return OAuth2Vendor.KAKAO;
    }

    @Override
    public OAuth2Properties properties() {
        return clientProperties.clients().get(platform());
    }

    @Override
    public OAuth2VendorEndpointBuilder endpointBuilder() {
        return new KakaoOAuth2EndpointBuilder();
    }

    @Override
    public OAuth2VendorAccessTokenFetcher accessTokenFetcher() {
        return new KakaoOAuth2AccessTokenFetcher();
    }

    @Override
    public OAuth2VendorUserProfileResolver userProfileResolver() {
        return new KakaoOAuth2UserProfileResolver();
    }
}
