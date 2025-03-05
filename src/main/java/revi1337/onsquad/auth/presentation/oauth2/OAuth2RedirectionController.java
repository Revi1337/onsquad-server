package revi1337.onsquad.auth.presentation.oauth2;

import java.net.URI;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;

@RestController
public class OAuth2RedirectionController {

    private final Map<String, OAuth2Properties> oauth2Properties;

    public OAuth2RedirectionController(OAuth2ClientProperties oAuth2ClientProperties) {
        this.oauth2Properties = oAuth2ClientProperties.clients();
    }

    @GetMapping("/api/v1/login/oauth2/{platform}")
    public ResponseEntity<Void> handlePlatformOAuth2Login(@PathVariable String platform) {
        if (platform.equals("kakao")) {
            OAuth2Properties oAuth2Properties = oauth2Properties.get("kakao");
            URI compositeAuthorizationEndPoint = buildCompositeKakaoAuthorizationEndPoint(oAuth2Properties);

            return ResponseEntity.status(HttpStatus.FOUND).location(compositeAuthorizationEndPoint).build();
        }
        if (platform.equals("google")) {
            OAuth2Properties oAuth2Properties = oauth2Properties.get("google");
            URI compositeAuthorizationEndPoint = buildCompositeGoogleAuthorizationEndPoint(oAuth2Properties);

            return ResponseEntity.status(HttpStatus.FOUND).location(compositeAuthorizationEndPoint).build();
        }
        throw new UnsupportedOperationException("unsupported authorization server");
    }

    private URI buildCompositeKakaoAuthorizationEndPoint(OAuth2Properties oAuth2Properties) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        return ServletUriComponentsBuilder
                .fromHttpUrl(oAuth2Properties.authorizationUri())
                .queryParam("client_id", oAuth2Properties.clientId())
                .queryParam("redirect_uri", baseUrl + oAuth2Properties.redirectUri())
                .queryParam("response_type", oAuth2Properties.responseType())
                .build()
                .toUri();
    }

    private URI buildCompositeGoogleAuthorizationEndPoint(OAuth2Properties oAuth2Properties) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        return ServletUriComponentsBuilder
                .fromHttpUrl(oAuth2Properties.authorizationUri())
                .queryParam("client_id", oAuth2Properties.clientId())
                .queryParam("redirect_uri", baseUrl + oAuth2Properties.redirectUri())
                .queryParam("response_type", oAuth2Properties.responseType())
                .queryParam("scope", String.join(" ", oAuth2Properties.scope().values()))
                .build()
                .toUri();
    }
}

//package revi1337.onsquad.auth.presentation.oauth2;
//
//import java.net.URI;
//import java.util.Map;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
//import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
//import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties.OAuth2Properties;
//
//@RestController
//public class OAuth2Controller {
//
//    private final Map<String, OAuth2Properties> oauth2Properties;
//
//    public OAuth2Controller(OAuth2ClientProperties oAuth2ClientProperties) {
//        this.oauth2Properties = oAuth2ClientProperties.clients();
//    }
//
//    /**
//     * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-code
//     */
//    @GetMapping("/api/v1/login/oauth2/kakao")
//    public ResponseEntity<Void> handleKakaoOAuth2Login() {
//        OAuth2Properties oAuth2Properties = oauth2Properties.get("kakao");
//        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
//        URI compositeAuthorizationEndPoint = ServletUriComponentsBuilder
//                .fromHttpUrl(oAuth2Properties.authorizationUri())
//                .queryParam("client_id", oAuth2Properties.clientId())
//                .queryParam("redirect_uri", baseUrl + oAuth2Properties.redirectUri())
//                .queryParam("response_type", "code")
//                .build()
//                .toUri();
//
//        return ResponseEntity.status(HttpStatus.FOUND).location(compositeAuthorizationEndPoint).build();
//    }
//
//
//    /**
//     * https://developers.google.com/identity/protocols/oauth2/web-server?hl=ko#redirecting
//     */
//    @GetMapping("/api/v1/login/oauth2/google")
//    public ResponseEntity<Void> handleGoogleOAuth2Login() {
//        OAuth2Properties oAuth2Properties = oauth2Properties.get("google");
//        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
//        URI compositeAuthorizationEndPoint = ServletUriComponentsBuilder
//                .fromHttpUrl(oAuth2Properties.authorizationUri())
//                .queryParam("client_id", oAuth2Properties.clientId())
//                .queryParam("redirect_uri", baseUrl + oAuth2Properties.redirectUri())
//                .queryParam("response_type", "code")
//                .queryParam("scope", String.join(" ", oAuth2Properties.scope().values()))
//                .build()
//                .toUri();
//
//        return ResponseEntity.status(HttpStatus.FOUND).location(compositeAuthorizationEndPoint).build();
//    }
//}
