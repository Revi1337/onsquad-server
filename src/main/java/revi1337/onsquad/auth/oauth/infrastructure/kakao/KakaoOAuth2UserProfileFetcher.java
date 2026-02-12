package revi1337.onsquad.auth.oauth.infrastructure.kakao;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import revi1337.onsquad.auth.oauth.application.PlatformOAuth2UserProfileFetcher;
import revi1337.onsquad.auth.oauth.config.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.auth.oauth.domain.PlatformUserProfile;
import revi1337.onsquad.token.domain.model.AccessToken;

public class KakaoOAuth2UserProfileFetcher implements PlatformOAuth2UserProfileFetcher {

    @Override
    public PlatformUserProfile fetch(AccessToken accessToken, OAuth2Properties oAuth2Properties) {
        KakaoUserInfoResponse kakaoUserInfoResponse = fetchUserInfoResponse(accessToken, oAuth2Properties);
        return KakaoUserProfile.from(kakaoUserInfoResponse);
    }

    private KakaoUserInfoResponse fetchUserInfoResponse(AccessToken accessToken,
                                                        OAuth2Properties oAuth2Properties) {
        MultiValueMap<String, String> userInfoHeaders = new LinkedMultiValueMap<>() {{
            add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken.value()));
            add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;");
            add(HttpHeaders.CONTENT_TYPE, "charset=utf-8");
        }};
        ResponseEntity<KakaoUserInfoResponse> kakaoProfileResponse = new RestTemplate().exchange(
                oAuth2Properties.accountUri(),
                HttpMethod.GET,
                new HttpEntity<>(userInfoHeaders),
                KakaoUserInfoResponse.class
        );

        return kakaoProfileResponse.getBody();
    }
}
