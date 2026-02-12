package revi1337.onsquad.auth.oauth.application;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import revi1337.onsquad.auth.oauth.application.contract.OAuth2VendorAccessTokenFetcher;
import revi1337.onsquad.auth.oauth.application.contract.OAuth2VendorEndpointBuilder;
import revi1337.onsquad.auth.oauth.application.contract.OAuth2VendorProvider;
import revi1337.onsquad.auth.oauth.application.contract.OAuth2VendorUserProfile;
import revi1337.onsquad.auth.oauth.application.contract.OAuth2VendorUserProfileResolver;
import revi1337.onsquad.common.config.system.properties.OnsquadProperties;
import revi1337.onsquad.token.domain.model.AccessToken;
import revi1337.onsquad.token.domain.model.JsonWebToken;

@Service
@RequiredArgsConstructor
public class OAuth2ExchangeService {

    private static final String ACCESS_TOKEN_PARAMETER = "accessToken";
    private static final String REFRESH_TOKEN_PARAMETER = "refreshToken";

    private final OnsquadProperties onsquadProperties;
    private final SocialMemberService loginService;
    private final OAuth2VendorRegistry vendorRegistry;

    public URI buildAuthorizationEndpoint(String platform, String baseUrl) {
        OAuth2VendorProvider vendorProvider = vendorRegistry.getOAuth2Provider(platform);
        OAuth2VendorEndpointBuilder endpointBuilder = vendorProvider.endpointBuilder();

        return endpointBuilder.build(baseUrl, vendorProvider.properties());
    }

    public URI handleOAuth2Login(String platform, String baseUrl, String code) {
        OAuth2VendorUserProfile userProfile = fetchUserProfile(baseUrl, platform, code);
        JsonWebToken jsonWebToken = loginService.authenticate(userProfile);

        return buildRedirectUri(jsonWebToken);
    }

    private OAuth2VendorUserProfile fetchUserProfile(String baseUrl, String platform, String authorizationCode) {
        OAuth2VendorProvider vendorProvider = vendorRegistry.getOAuth2Provider(platform);
        OAuth2VendorAccessTokenFetcher tokenFetcher = vendorProvider.accessTokenFetcher();
        OAuth2VendorUserProfileResolver userProfileResolver = vendorProvider.userProfileResolver();

        AccessToken accessToken = tokenFetcher.fetch(baseUrl, authorizationCode, vendorProvider.properties());
        return userProfileResolver.fetch(accessToken, vendorProvider.properties());
    }

    private URI buildRedirectUri(JsonWebToken jsonWebToken) {
        return UriComponentsBuilder.fromHttpUrl(onsquadProperties.getFrontendBaseUrl())
                .queryParam(ACCESS_TOKEN_PARAMETER, jsonWebToken.accessToken())
                .queryParam(REFRESH_TOKEN_PARAMETER, jsonWebToken.refreshToken())
                .build()
                .toUri();
    }
}
