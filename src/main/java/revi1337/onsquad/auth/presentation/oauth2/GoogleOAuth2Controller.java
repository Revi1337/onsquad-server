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
//import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
//import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;
//import revi1337.onsquad.auth.presentation.oauth2.dto.response.GoogleUserInfoResponse;
//
//@Deprecated(forRemoval = true)
//@RestController
//public class GoogleOAuth2Controller {
//
//    private final Map<String, OAuth2Properties> oauth2Properties;
//
//    public GoogleOAuth2Controller(OAuth2ClientProperties oAuth2ClientProperties) {
//        this.oauth2Properties = oAuth2ClientProperties.clients();
//    }
//
//    /**
//     * https://developers.google.com/identity/protocols/oauth2/web-server?hl=ko#exchange-authorization-code
//     */
//    @GetMapping("/login/oauth2/code/google")
//    public ResponseEntity<Object> receiveAuthorizationCode(@RequestParam String code) {
//        OAuth2Properties oAuth2Properties = oauth2Properties.get("google");
//        String accessToken = fetchAccessToken(code, oAuth2Properties);
//        GoogleUserInfoResponse googleUserInfoResponse = fetchUserInfoResponse(accessToken, oAuth2Properties);
//
//        return ResponseEntity.ok(googleUserInfoResponse);
//    }
//
//    private String fetchAccessToken(String code, OAuth2Properties oAuth2Properties) {
//        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>() {{
//            add("client_id", oAuth2Properties.clientId());
//            add("client_secret", oAuth2Properties.clientSecret());
//            add("redirect_uri", baseUrl + oAuth2Properties.redirectUri());
//            add("grant_type", oAuth2Properties.grantType());
//            add("code", code);
//        }};
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
//    private GoogleUserInfoResponse fetchUserInfoResponse(String accessToken, OAuth2Properties oAuth2Properties) {
//        MultiValueMap<String, String> userInfoHeaders = new LinkedMultiValueMap<>();
//        userInfoHeaders.add(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", accessToken));
//        ResponseEntity<GoogleUserInfoResponse> googleProfileResponse = new RestTemplate().exchange(
//                oAuth2Properties.accountUri(),
//                HttpMethod.GET,
//                new HttpEntity<>(userInfoHeaders),
//                GoogleUserInfoResponse.class
//        );
//
//        return googleProfileResponse.getBody();
//    }
//}
