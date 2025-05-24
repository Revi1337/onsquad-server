package revi1337.onsquad.auth.presentation.oauth;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import revi1337.onsquad.auth.application.oauth.OAuth2Service;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class PlatformOAuth2CodeGrantController {

    private final OAuth2Service oAuth2Service;

    @GetMapping("/login/oauth2/code/{platform}")
    public ResponseEntity<Void> receivePlatformAuthorizationCode(
            @PathVariable String platform,
            @RequestParam String code
    ) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        URI redirectUri = oAuth2Service.handleOAuth2Login(baseUrl, platform, code);

        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
    }
}
