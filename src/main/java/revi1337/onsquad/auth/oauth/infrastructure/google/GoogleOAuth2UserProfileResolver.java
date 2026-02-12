package revi1337.onsquad.auth.oauth.infrastructure.google;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import revi1337.onsquad.auth.oauth.application.contract.OAuth2VendorUserProfile;
import revi1337.onsquad.auth.oauth.application.contract.OAuth2VendorUserProfileResolver;
import revi1337.onsquad.auth.oauth.infrastructure.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.token.domain.model.AccessToken;

public class GoogleOAuth2UserProfileResolver implements OAuth2VendorUserProfileResolver {

    @Override
    public OAuth2VendorUserProfile fetch(AccessToken accessToken, OAuth2Properties oAuth2Properties) {
        GoogleUserInfoResponse googleUserInfoResponse = fetchUserInfoResponse(accessToken, oAuth2Properties);
        return GoogleOAuth2UserProfile.from(googleUserInfoResponse);
    }

    private GoogleUserInfoResponse fetchUserInfoResponse(AccessToken accessToken, OAuth2Properties oAuth2Properties) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken.value());
        ResponseEntity<GoogleUserInfoResponse> googleProfileResponse = new RestTemplate().exchange(
                oAuth2Properties.accountUri(),
                HttpMethod.GET,
                new HttpEntity<>(httpHeaders),
                GoogleUserInfoResponse.class
        );

        return googleProfileResponse.getBody();
    }
}
