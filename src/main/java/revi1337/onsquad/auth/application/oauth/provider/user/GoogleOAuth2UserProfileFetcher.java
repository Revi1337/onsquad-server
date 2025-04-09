package revi1337.onsquad.auth.application.oauth.provider.user;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.auth.model.oauth.GoogleUserProfile;
import revi1337.onsquad.auth.model.oauth.PlatformUserProfile;
import revi1337.onsquad.auth.model.token.AccessToken;
import revi1337.onsquad.auth.presentation.oauth.dto.response.GoogleUserInfoResponse;

public class GoogleOAuth2UserProfileFetcher implements PlatformOAuth2UserProfileFetcher {

    @Override
    public PlatformUserProfile fetchUserProfile(AccessToken accessToken, OAuth2Properties oAuth2Properties) {
        GoogleUserInfoResponse googleUserInfoResponse = fetchUserInfoResponse(accessToken, oAuth2Properties);
        return GoogleUserProfile.from(googleUserInfoResponse);
    }

    private GoogleUserInfoResponse fetchUserInfoResponse(AccessToken accessToken,
                                                         OAuth2Properties oAuth2Properties) {
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
