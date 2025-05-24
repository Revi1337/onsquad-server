package revi1337.onsquad.auth.presentation.oauth;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import revi1337.onsquad.auth.application.oauth.OAuth2Service;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class OAuth2RedirectionController {

    private final OAuth2Service oAuth2Service;

    @GetMapping("/login/oauth2/{platform}")
    public ResponseEntity<String> buildAuthorizationEndpoint(@PathVariable String platform) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        URI authorizationEndpoint = oAuth2Service.buildAuthorizationEndpoint(baseUrl, platform);

        return ResponseEntity.ok().location(authorizationEndpoint).build();
    }
}
