package revi1337.onsquad.auth.config.properties;

import java.net.URI;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import revi1337.onsquad.auth.application.oauth2.AuthorizationAccessTokenProvider;
import revi1337.onsquad.auth.application.oauth2.AuthorizationEndPointProvider;
import revi1337.onsquad.auth.application.oauth2.AuthorizationUserProfileProvider;
import revi1337.onsquad.auth.application.oauth2.model.GoogleUserProfile;
import revi1337.onsquad.auth.application.oauth2.model.KakaoUserProfile;
import revi1337.onsquad.auth.application.oauth2.model.PlatformUserProfile;
import revi1337.onsquad.auth.application.token.AccessToken;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.auth.presentation.oauth2.dto.response.GoogleUserInfoResponse;
import revi1337.onsquad.auth.presentation.oauth2.dto.response.KakaoUserInfoResponse;

public enum SupportOAuth2Platform implements AuthorizationEndPointProvider,
        AuthorizationAccessTokenProvider, AuthorizationUserProfileProvider {

    KAKAO {
        @Override
        public URI provideUsing(String baseUrl, OAuth2ClientProperties oAuth2ClientProperties) {
            OAuth2Properties oAuth2Properties = getPropertyFrom(oAuth2ClientProperties);
            return buildKakakoAuthorizationUri(baseUrl, oAuth2Properties);
        }

        @Override
        public AccessToken provideAccessToken(String baseUrl, String authorizationCode,
                                              OAuth2ClientProperties oAuth2ClientProperties) {
            OAuth2Properties oAuth2Properties = getPropertyFrom(oAuth2ClientProperties);
            return fetchKakaoAccessToken(baseUrl, authorizationCode, oAuth2Properties);
        }

        @Override
        public PlatformUserProfile provideUserProfile(String baseUrl, AccessToken accessToken,
                                                      OAuth2ClientProperties oAuth2ClientProperties) {
            OAuth2Properties oAuth2Properties = getPropertyFrom(oAuth2ClientProperties);
            KakaoUserInfoResponse kakaoUserInfoResponse = fetchKakaoUserInfoResponse(accessToken, oAuth2Properties);
            return KakaoUserProfile.from(kakaoUserInfoResponse);
        }
    },

    GOOGLE {
        @Override
        public URI provideUsing(String baseUrl, OAuth2ClientProperties oAuth2ClientProperties) {
            OAuth2Properties oAuth2Properties = getPropertyFrom(oAuth2ClientProperties);
            return buildGoogleAuthorizationUri(baseUrl, oAuth2Properties);
        }

        @Override
        public AccessToken provideAccessToken(String baseUrl, String authorizationCode,
                                              OAuth2ClientProperties oAuth2ClientProperties) {
            OAuth2Properties oAuth2Properties = getPropertyFrom(oAuth2ClientProperties);
            return fetchGoogleAccessToken(baseUrl, authorizationCode, oAuth2Properties);
        }

        @Override
        public PlatformUserProfile provideUserProfile(String baseUrl, AccessToken accessToken,
                                                      OAuth2ClientProperties oAuth2ClientProperties) {
            OAuth2Properties oAuth2Properties = getPropertyFrom(oAuth2ClientProperties);
            GoogleUserInfoResponse googleUserInfoResponse = fetchGoogleUserInfoResponse(accessToken, oAuth2Properties);
            return GoogleUserProfile.from(googleUserInfoResponse);
        }
    };

    URI buildKakakoAuthorizationUri(String baseUrl, OAuth2Properties oAuth2Properties) {
        return ServletUriComponentsBuilder
                .fromHttpUrl(oAuth2Properties.authorizationUri())
                .queryParam("client_id", oAuth2Properties.clientId())
                .queryParam("redirect_uri", baseUrl + oAuth2Properties.redirectUri())
                .queryParam("response_type", oAuth2Properties.responseType())
                .build()
                .toUri();
    }

    AccessToken fetchKakaoAccessToken(String baseUrl, String authorizationCode, OAuth2Properties oAuth2Properties) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            add("client_id", oAuth2Properties.clientId());
            add("client_secret", oAuth2Properties.clientSecret());
            add("redirect_uri", baseUrl + oAuth2Properties.redirectUri());
            add("grant_type", oAuth2Properties.grantType());
            add("code", authorizationCode);
        }};

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<Map<String, String>> tokenAttributeResponse = new RestTemplate().exchange(
                oAuth2Properties.tokenUri(),
                HttpMethod.POST,
                new HttpEntity<>(params, headers),
                new ParameterizedTypeReference<>() {
                }
        );

        return AccessToken.of(tokenAttributeResponse.getBody().get("access_token"));
    }

    KakaoUserInfoResponse fetchKakaoUserInfoResponse(AccessToken accessToken,
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

    URI buildGoogleAuthorizationUri(String baseUrl, OAuth2Properties oAuth2Properties) {
        return ServletUriComponentsBuilder
                .fromHttpUrl(oAuth2Properties.authorizationUri())
                .queryParam("client_id", oAuth2Properties.clientId())
                .queryParam("redirect_uri", baseUrl + oAuth2Properties.redirectUri())
                .queryParam("response_type", oAuth2Properties.responseType())
                .queryParam("scope", String.join(" ", oAuth2Properties.scope().values()))
                .build()
                .toUri();
    }

    AccessToken fetchGoogleAccessToken(String baseUrl, String authorizationCode,
                                       OAuth2Properties oAuth2Properties) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            add("client_id", oAuth2Properties.clientId());
            add("client_secret", oAuth2Properties.clientSecret());
            add("redirect_uri", baseUrl + oAuth2Properties.redirectUri());
            add("grant_type", oAuth2Properties.grantType());
            add("code", authorizationCode);
        }};

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        ResponseEntity<Map<String, String>> tokenAttributeResponse = new RestTemplate().exchange(
                oAuth2Properties.tokenUri(),
                HttpMethod.POST,
                new HttpEntity<>(params, headers),
                new ParameterizedTypeReference<>() {
                }
        );

        return AccessToken.of(tokenAttributeResponse.getBody().get("access_token"));
    }

    GoogleUserInfoResponse fetchGoogleUserInfoResponse(AccessToken accessToken,
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

    public static SupportOAuth2Platform convertFrom(String platform) {
        try {
            String ignoreCaseOAuthorizationPlatform = platform.toUpperCase();
            return valueOf(ignoreCaseOAuthorizationPlatform);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("unsupported authorization platform " + platform);
        }
    }

    public OAuth2Properties getPropertyFrom(OAuth2ClientProperties oAuth2ClientProperties) {
        return oAuth2ClientProperties.clients().get(this);
    }
}

// #################################################################################
//package revi1337.onsquad.auth.config.properties;
//
//import java.net.URI;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//import revi1337.onsquad.auth.application.oauth2.CompletedAuthorizationEndPointProvider;
//import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;
//
//public enum SupportOAuth2Platform implements CompletedAuthorizationEndPointProvider {
//
//    KAKAO {
//        @Override
//        public URI provideUsing(String baseUrl, OAuth2ClientProperties oAuth2ClientProperties) {
//            OAuth2Properties oAuth2Properties = getPropertyFrom(oAuth2ClientProperties);
//            return ServletUriComponentsBuilder
//                    .fromHttpUrl(oAuth2Properties.authorizationUri())
//                    .queryParam("client_id", oAuth2Properties.clientId())
//                    .queryParam("redirect_uri", baseUrl + oAuth2Properties.redirectUri())
//                    .queryParam("response_type", oAuth2Properties.responseType())
//                    .build()
//                    .toUri();
//        }
//    },
//
//    GOOGLE {
//        @Override
//        public URI provideUsing(String baseUrl, OAuth2ClientProperties oAuth2ClientProperties) {
//            OAuth2Properties oAuth2Properties = getPropertyFrom(oAuth2ClientProperties);
//            return ServletUriComponentsBuilder
//                    .fromHttpUrl(oAuth2Properties.authorizationUri())
//                    .queryParam("client_id", oAuth2Properties.clientId())
//                    .queryParam("redirect_uri", baseUrl + oAuth2Properties.redirectUri())
//                    .queryParam("response_type", oAuth2Properties.responseType())
//                    .queryParam("scope", String.join(" ", oAuth2Properties.scope().values()))
//                    .build()
//                    .toUri();
//        }
//    };
//
//    public static SupportOAuth2Platform convertFrom(String platform) {
//        try {
//            String ignoreCaseOAuthorizationPlatform = platform.toUpperCase();
//            return valueOf(ignoreCaseOAuthorizationPlatform);
//        } catch (IllegalArgumentException e) {
//            throw new UnsupportedOperationException("unsupported authorization platform " + platform);
//        }
//    }
//
//    public OAuth2Properties getPropertyFrom(OAuth2ClientProperties oAuth2ClientProperties) {
//        return oAuth2ClientProperties.clients().get(this);
//    }
//}
