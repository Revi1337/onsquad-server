//package revi1337.onsquad.auth.presentation.oauth2;
//
//import java.util.Map;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
//import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;
//import revi1337.onsquad.auth.presentation.oauth2.dto.response.KakaoUserInfoResponse;
//
//@Deprecated(forRemoval = true)
//@RestController
//public class KakaoOAuth2Controller {
//
//    private final Map<String, OAuth2Properties> oauth2Properties;
//
//    public KakaoOAuth2Controller(OAuth2ClientProperties oAuth2ClientProperties) {
//        this.oauth2Properties = oAuth2ClientProperties.clients();
//    }
//
//    /**
//     * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token
//     * <p>
//     * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
//     */
//    @GetMapping("/login/oauth2/code/kakao")
//    public ResponseEntity<KakaoUserInfoResponse> receiveAuthorizationCode(@RequestParam String code) {
//        OAuth2Properties oAuth2Properties = oauth2Properties.get("kakao");
//        String accessToken = fetchAccessToken(code, oAuth2Properties);
//        KakaoUserInfoResponse kakaoUserInfoResponse = fetchUserInfoResponse(accessToken, oAuth2Properties);
//
//        return ResponseEntity.ok(kakaoUserInfoResponse);
//    }
//
//    private String fetchAccessToken(String code, OAuth2Properties oAuth2Properties) {
//        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
//        String compositeTokenEndPoint = ServletUriComponentsBuilder
//                .fromHttpUrl(oAuth2Properties.tokenUri())
//                .queryParam("client_id", oAuth2Properties.clientId())
//                .queryParam("grant_type", oAuth2Properties.grantType())
//                .queryParam("redirect_uri", baseUrl + oAuth2Properties.redirectUri())
//                .queryParam("code", code)
//                .toUriString();
//        MultiValueMap<String, String> tokenAttributeHeader = new LinkedMultiValueMap<>();
//        tokenAttributeHeader.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;");
//        tokenAttributeHeader.add(HttpHeaders.CONTENT_TYPE, "charset=utf-8");
//        ResponseEntity<Map<String, String>> tokenAttributeResponse = new RestTemplate().exchange(
//                compositeTokenEndPoint, HttpMethod.POST, new HttpEntity<>(tokenAttributeHeader),
//                new ParameterizedTypeReference<>() {
//                }
//        );
//
//        return tokenAttributeResponse.getBody().get("access_token");
//    }
//
//    private KakaoUserInfoResponse fetchUserInfoResponse(String accessToken, OAuth2Properties oAuth2Properties) {
//        MultiValueMap<String, String> userInfoHeaders = new LinkedMultiValueMap<>();
//        userInfoHeaders.add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken));
//        userInfoHeaders.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;");
//        userInfoHeaders.add(HttpHeaders.CONTENT_TYPE, "charset=utf-8");
//        ResponseEntity<KakaoUserInfoResponse> kakaoProfileResponse = new RestTemplate().exchange(
//                oAuth2Properties.accountUri(),
//                HttpMethod.GET,
//                new HttpEntity<>(userInfoHeaders),
//                KakaoUserInfoResponse.class
//        );
//
//        return kakaoProfileResponse.getBody();
//    }
//}
//
////        // fetch OIDC user info
////        MultiValueMap<String, String> oidcHeaders = new LinkedMultiValueMap<>();
////        oidcHeaders.add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken));
////        ResponseEntity<Map<String, String>> oidcResponse = new RestTemplate().exchange(
////                oAuth2Properties.userInfoUri(),
////                HttpMethod.GET,
////                new HttpEntity<>(oidcHeaders),
////                new ParameterizedTypeReference<>() {
////                }
////        );
////        Map<String, String> oidcBody = oidcResponse.getBody();
////
