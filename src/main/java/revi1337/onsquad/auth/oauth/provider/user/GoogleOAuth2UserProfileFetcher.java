package revi1337.onsquad.auth.oauth.provider.user;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import revi1337.onsquad.auth.oauth.config.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.auth.oauth.profile.PlatformUserProfile;
import revi1337.onsquad.auth.oauth.profile.user.GoogleUserProfile;
import revi1337.onsquad.auth.oauth.provider.PlatformOAuth2UserProfileFetcher;
import revi1337.onsquad.auth.oauth.provider.user.response.GoogleUserInfoResponse;
import revi1337.onsquad.token.domain.model.AccessToken;

public class GoogleOAuth2UserProfileFetcher implements PlatformOAuth2UserProfileFetcher {

    @Override
    public PlatformUserProfile fetch(AccessToken accessToken, OAuth2Properties oAuth2Properties) {
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
