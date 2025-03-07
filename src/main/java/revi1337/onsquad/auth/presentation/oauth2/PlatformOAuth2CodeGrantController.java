package revi1337.onsquad.auth.presentation.oauth2;

import java.net.URI;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import revi1337.onsquad.auth.application.dto.JsonWebToken;
import revi1337.onsquad.auth.application.oauth2.OAuth2LoginService;
import revi1337.onsquad.auth.application.oauth2.model.GoogleUserProfile;
import revi1337.onsquad.auth.application.oauth2.model.KakaoUserProfile;
import revi1337.onsquad.auth.application.oauth2.model.PlatformUserProfile;
import revi1337.onsquad.auth.application.token.AccessToken;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.auth.presentation.oauth2.dto.response.GoogleUserInfoResponse;
import revi1337.onsquad.auth.presentation.oauth2.dto.response.KakaoUserInfoResponse;
import revi1337.onsquad.common.config.properties.OnsquadProperties;

@RestController
public class PlatformOAuth2CodeGrantController {

    private final Map<String, OAuth2Properties> oauth2Properties;
    private final OnsquadProperties onsquadProperties;
    private final OAuth2LoginService oAuth2LoginService;

    public PlatformOAuth2CodeGrantController(OAuth2ClientProperties oAuth2ClientProperties,
                                             OnsquadProperties onsquadProperties,
                                             OAuth2LoginService oAuth2LoginService) {
        this.oauth2Properties = oAuth2ClientProperties.clients();
        this.onsquadProperties = onsquadProperties;
        this.oAuth2LoginService = oAuth2LoginService;
    }

    @GetMapping("/login/oauth2/code/{platform}")
    public ResponseEntity<Void> receivePlatformAuthorizationCode(@PathVariable String platform,
                                                                 @RequestParam String code) {
        if (platform.equals("kakao")) {
            OAuth2Properties oAuth2Properties = oauth2Properties.get("kakao");
            AccessToken accessToken = fetchKakaoAccessToken(code, oAuth2Properties);
            KakaoUserInfoResponse kakaoUserInfoResponse = fetchKakaoUserInfoResponse(accessToken, oAuth2Properties);
            PlatformUserProfile kakaoUserProfile = KakaoUserProfile.from(kakaoUserInfoResponse);

            JsonWebToken jsonWebToken = oAuth2LoginService.loginOAuth2User(kakaoUserProfile);
            URI redirectUri = buildRedirectUri(jsonWebToken);

            return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
        }
        if (platform.equals("google")) {
            OAuth2Properties oAuth2Properties = oauth2Properties.get("google");
            AccessToken accessToken = fetchGoogleAccessToken(code, oAuth2Properties);
            GoogleUserInfoResponse googleUserInfoResponse = fetchGoogleUserInfoResponse(accessToken, oAuth2Properties);
            PlatformUserProfile googleUserProfile = GoogleUserProfile.from(googleUserInfoResponse);

            JsonWebToken jsonWebToken = oAuth2LoginService.loginOAuth2User(googleUserProfile);
            URI redirectUri = buildRedirectUri(jsonWebToken);

            return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
        }

        throw new IllegalArgumentException("unsupported platform: " + platform);
    }

    private AccessToken fetchKakaoAccessToken(String code, OAuth2Properties oAuth2Properties) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        String compositeTokenEndPoint = ServletUriComponentsBuilder
                .fromHttpUrl(oAuth2Properties.tokenUri())
                .queryParam("client_id", oAuth2Properties.clientId())
                .queryParam("grant_type", oAuth2Properties.grantType())
                .queryParam("redirect_uri", baseUrl + oAuth2Properties.redirectUri())
                .queryParam("code", code)
                .toUriString();

        MultiValueMap<String, String> tokenAttributeHeader = new LinkedMultiValueMap<>() {{
            add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;");
            add(HttpHeaders.CONTENT_TYPE, "charset=utf-8");
        }};
        ResponseEntity<Map<String, String>> tokenAttributeResponse = new RestTemplate().exchange(
                compositeTokenEndPoint, HttpMethod.POST, new HttpEntity<>(tokenAttributeHeader),
                new ParameterizedTypeReference<>() {
                }
        );

        return AccessToken.of(tokenAttributeResponse.getBody().get("access_token"));
    }

    private KakaoUserInfoResponse fetchKakaoUserInfoResponse(AccessToken accessToken,
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

    private AccessToken fetchGoogleAccessToken(String code, OAuth2Properties oAuth2Properties) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
            add("client_id", oAuth2Properties.clientId());
            add("client_secret", oAuth2Properties.clientSecret());
            add("redirect_uri", baseUrl + oAuth2Properties.redirectUri());
            add("grant_type", oAuth2Properties.grantType());
            add("code", code);
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

    private GoogleUserInfoResponse fetchGoogleUserInfoResponse(AccessToken accessToken,
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

    private URI buildRedirectUri(JsonWebToken jsonWebToken) {
        return ServletUriComponentsBuilder.fromHttpUrl(onsquadProperties.getFrontendBaseUrl())
                .queryParam("accessToken", jsonWebToken.accessToken().value())
                .queryParam("refreshToken", jsonWebToken.refreshToken().value())
                .build()
                .toUri();
    }
}

//package revi1337.onsquad.auth.presentation.oauth2;
//
//import java.util.Map;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//import revi1337.onsquad.auth.application.dto.JsonWebToken;
//import revi1337.onsquad.auth.application.oauth2.OAuth2LoginService;
//import revi1337.onsquad.auth.application.oauth2.model.GoogleUserProfile;
//import revi1337.onsquad.auth.application.oauth2.model.KakaoUserProfile;
//import revi1337.onsquad.auth.application.oauth2.model.PlatformUserProfile;
//import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
//import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;
//import revi1337.onsquad.auth.presentation.oauth2.dto.response.GoogleUserInfoResponse;
//import revi1337.onsquad.auth.presentation.oauth2.dto.response.KakaoUserInfoResponse;
//
//@RestController
//public class PlatformOAuth2CodeGrantController {
//
//    private final Map<String, OAuth2Properties> oauth2Properties;
//    private final OAuth2LoginService oAuth2LoginService;
//
//    public PlatformOAuth2CodeGrantController(OAuth2ClientProperties oAuth2ClientProperties,
//                                             OAuth2LoginService oAuth2LoginService) {
//        this.oauth2Properties = oAuth2ClientProperties.clients();
//        this.oAuth2LoginService = oAuth2LoginService;
//    }
//
//    @GetMapping("/login/oauth2/code/{platform}")
//    public ResponseEntity<JsonWebToken> receivePlatformAuthorizationCode(@PathVariable String platform,
//                                                                         @RequestParam String code) {
//        if (platform.equals("kakao")) {
//            OAuth2Properties oAuth2Properties = oauth2Properties.get("kakao");
//            String accessToken = fetchKakaoAccessToken(code, oAuth2Properties);
//            KakaoUserInfoResponse kakaoUserInfoResponse = fetchKakaoUserInfoResponse(accessToken, oAuth2Properties);
//            PlatformUserProfile kakaoUserProfile = KakaoUserProfile.from(kakaoUserInfoResponse);
//
//            JsonWebToken jsonWebToken = oAuth2LoginService.loginOAuth2User(kakaoUserProfile);
//
//            return ResponseEntity.ok(jsonWebToken);
//        }
//        if (platform.equals("google")) {
//            OAuth2Properties oAuth2Properties = oauth2Properties.get("google");
//            String accessToken = fetchGoogleAccessToken(code, oAuth2Properties);
//            GoogleUserInfoResponse googleUserInfoResponse = fetchGoogleUserInfoResponse(accessToken, oAuth2Properties);
//            PlatformUserProfile googleUserProfile = GoogleUserProfile.from(googleUserInfoResponse);
//
//            JsonWebToken jsonWebToken = oAuth2LoginService.loginOAuth2User(googleUserProfile);
//
//            return ResponseEntity.ok(jsonWebToken);
//        }
//
//        throw new IllegalArgumentException("unsupported platform: " + platform);
//    }
//
//    private String fetchKakaoAccessToken(String code, OAuth2Properties oAuth2Properties) {
//        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
//        String compositeTokenEndPoint = ServletUriComponentsBuilder
//                .fromHttpUrl(oAuth2Properties.tokenUri())
//                .queryParam("client_id", oAuth2Properties.clientId())
//                .queryParam("grant_type", oAuth2Properties.grantType())
//                .queryParam("redirect_uri", baseUrl + oAuth2Properties.redirectUri())
//                .queryParam("code", code)
//                .toUriString();
//
//        MultiValueMap<String, String> tokenAttributeHeader = new LinkedMultiValueMap<>() {{
//            add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;");
//            add(HttpHeaders.CONTENT_TYPE, "charset=utf-8");
//        }};
//        ResponseEntity<Map<String, String>> tokenAttributeResponse = new RestTemplate().exchange(
//                compositeTokenEndPoint, HttpMethod.POST, new HttpEntity<>(tokenAttributeHeader),
//                new ParameterizedTypeReference<>() {
//                }
//        );
//
//        return tokenAttributeResponse.getBody().get("access_token");
//    }
//
//    private KakaoUserInfoResponse fetchKakaoUserInfoResponse(String accessToken, OAuth2Properties oAuth2Properties) {
//        MultiValueMap<String, String> userInfoHeaders = new LinkedMultiValueMap<>() {{
//            add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken));
//            add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;");
//            add(HttpHeaders.CONTENT_TYPE, "charset=utf-8");
//        }};
//        ResponseEntity<KakaoUserInfoResponse> kakaoProfileResponse = new RestTemplate().exchange(
//                oAuth2Properties.accountUri(),
//                HttpMethod.GET,
//                new HttpEntity<>(userInfoHeaders),
//                KakaoUserInfoResponse.class
//        );
//
//        return kakaoProfileResponse.getBody();
//    }
//
//    private String fetchGoogleAccessToken(String code, OAuth2Properties oAuth2Properties) {
//        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
//            add("client_id", oAuth2Properties.clientId());
//            add("client_secret", oAuth2Properties.clientSecret());
//            add("redirect_uri", baseUrl + oAuth2Properties.redirectUri());
//            add("grant_type", oAuth2Properties.grantType());
//            add("code", code);
//        }};
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        ResponseEntity<Map<String, String>> tokenAttributeResponse = new RestTemplate().exchange(
//                oAuth2Properties.tokenUri(),
//                HttpMethod.POST,
//                new HttpEntity<>(params, headers),
//                new ParameterizedTypeReference<>() {
//                }
//        );
//
//        return tokenAttributeResponse.getBody().get("access_token");
//    }
//
//    private GoogleUserInfoResponse fetchGoogleUserInfoResponse(String accessToken, OAuth2Properties oAuth2Properties) {
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setBearerAuth(accessToken);
//        ResponseEntity<GoogleUserInfoResponse> googleProfileResponse = new RestTemplate().exchange(
//                oAuth2Properties.accountUri(),
//                HttpMethod.GET,
//                new HttpEntity<>(httpHeaders),
//                GoogleUserInfoResponse.class
//        );
//
//        return googleProfileResponse.getBody();
//    }
//}

//package revi1337.onsquad.auth.presentation.oauth2;
//
//import java.util.Map;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//import revi1337.onsquad.auth.application.oauth2.OAuth2LoginService;
//import revi1337.onsquad.auth.application.oauth2.PlatformUserProfileConverter;
//import revi1337.onsquad.auth.application.oauth2.model.GoogleUserProfile;
//import revi1337.onsquad.auth.application.oauth2.model.KakaoUserProfile;
//import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
//import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;
//import revi1337.onsquad.auth.presentation.oauth2.dto.response.GoogleUserInfoResponse;
//import revi1337.onsquad.auth.presentation.oauth2.dto.response.KakaoUserInfoResponse;
//
//@RestController
//public class PlatformOAuth2CodeGrantController {
//
//    private final Map<String, OAuth2Properties> oauth2Properties;
//    private final OAuth2LoginService oAuth2LoginService;
//
//    public PlatformOAuth2CodeGrantController(OAuth2ClientProperties oAuth2ClientProperties,
//                                             OAuth2LoginService oAuth2LoginService) {
//        this.oauth2Properties = oAuth2ClientProperties.clients();
//        this.oAuth2LoginService = oAuth2LoginService;
//    }
//
//    @GetMapping("/login/oauth2/code/kakao")
//    public ResponseEntity<KakaoUserProfile> receiveKakaoAuthorizationCode(@RequestParam String code) {
//        OAuth2Properties oAuth2Properties = oauth2Properties.get("kakao");
//        String accessToken = fetchKakaoAccessToken(code, oAuth2Properties);
//        KakaoUserInfoResponse kakaoUserInfoResponse = fetchKakaoUserInfoResponse(accessToken, oAuth2Properties);
//
//        PlatformUserProfileConverter platformUserProfileConverter = new PlatformUserProfileConverter();
//        KakaoUserProfile kakaoUserProfile = platformUserProfileConverter.convertKakaoProfile(kakaoUserInfoResponse);
//
//        return ResponseEntity.ok(kakaoUserProfile);
//    }
//
//    @GetMapping("/login/oauth2/code/google")
//    public ResponseEntity<GoogleUserProfile> receiveGoogleAuthorizationCode(@RequestParam String code) {
//        OAuth2Properties oAuth2Properties = oauth2Properties.get("google");
//        String accessToken = fetchGoogleAccessToken(code, oAuth2Properties);
//        GoogleUserInfoResponse googleUserInfoResponse = fetchGoogleUserInfoResponse(accessToken, oAuth2Properties);
//
//        PlatformUserProfileConverter platformUserProfileConverter = new PlatformUserProfileConverter();
//        GoogleUserProfile googleUserProfile = platformUserProfileConverter.convertGoogleProfile(googleUserInfoResponse);
//
//        return ResponseEntity.ok(googleUserProfile);
//    }
//
//    private String fetchKakaoAccessToken(String code, OAuth2Properties oAuth2Properties) {
//        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
//        String compositeTokenEndPoint = ServletUriComponentsBuilder
//                .fromHttpUrl(oAuth2Properties.tokenUri())
//                .queryParam("client_id", oAuth2Properties.clientId())
//                .queryParam("grant_type", oAuth2Properties.grantType())
//                .queryParam("redirect_uri", baseUrl + oAuth2Properties.redirectUri())
//                .queryParam("code", code)
//                .toUriString();
//
//        MultiValueMap<String, String> tokenAttributeHeader = new LinkedMultiValueMap<>() {{
//            add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;");
//            add(HttpHeaders.CONTENT_TYPE, "charset=utf-8");
//        }};
//        ResponseEntity<Map<String, String>> tokenAttributeResponse = new RestTemplate().exchange(
//                compositeTokenEndPoint, HttpMethod.POST, new HttpEntity<>(tokenAttributeHeader),
//                new ParameterizedTypeReference<>() {
//                }
//        );
//
//        return tokenAttributeResponse.getBody().get("access_token");
//    }
//
//    private KakaoUserInfoResponse fetchKakaoUserInfoResponse(String accessToken, OAuth2Properties oAuth2Properties) {
//        MultiValueMap<String, String> userInfoHeaders = new LinkedMultiValueMap<>() {{
//            add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken));
//            add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;");
//            add(HttpHeaders.CONTENT_TYPE, "charset=utf-8");
//        }};
//        ResponseEntity<KakaoUserInfoResponse> kakaoProfileResponse = new RestTemplate().exchange(
//                oAuth2Properties.accountUri(),
//                HttpMethod.GET,
//                new HttpEntity<>(userInfoHeaders),
//                KakaoUserInfoResponse.class
//        );
//
//        return kakaoProfileResponse.getBody();
//    }
//
//    private String fetchGoogleAccessToken(String code, OAuth2Properties oAuth2Properties) {
//        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
//            add("client_id", oAuth2Properties.clientId());
//            add("client_secret", oAuth2Properties.clientSecret());
//            add("redirect_uri", baseUrl + oAuth2Properties.redirectUri());
//            add("grant_type", oAuth2Properties.grantType());
//            add("code", code);
//        }};
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        ResponseEntity<Map<String, String>> tokenAttributeResponse = new RestTemplate().exchange(
//                oAuth2Properties.tokenUri(),
//                HttpMethod.POST,
//                new HttpEntity<>(params, headers),
//                new ParameterizedTypeReference<>() {
//                }
//        );
//
//        return tokenAttributeResponse.getBody().get("access_token");
//    }
//
//    private GoogleUserInfoResponse fetchGoogleUserInfoResponse(String accessToken, OAuth2Properties oAuth2Properties) {
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setBearerAuth(accessToken);
//        ResponseEntity<GoogleUserInfoResponse> googleProfileResponse = new RestTemplate().exchange(
//                oAuth2Properties.accountUri(),
//                HttpMethod.GET,
//                new HttpEntity<>(httpHeaders),
//                GoogleUserInfoResponse.class
//        );
//
//        return googleProfileResponse.getBody();
//    }
//}
