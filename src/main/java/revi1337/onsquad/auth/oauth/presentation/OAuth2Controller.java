package revi1337.onsquad.auth.oauth.presentation;

import static org.springframework.http.HttpStatus.FOUND;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import revi1337.onsquad.auth.oauth.application.OAuth2ExchangeService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final OAuth2ExchangeService oauth2Exchangeservice;

    @GetMapping("/login/oauth2/{vendor}")
    public ResponseEntity<String> buildAuthorizationEndpoint(@PathVariable String vendor) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        URI authorizationEndpoint = oauth2Exchangeservice.buildAuthorizationEndpoint(vendor, baseUrl);

        oauth2Exchangeservice.buildAuthorizationEndpoint(vendor, baseUrl);

        return ResponseEntity.ok().location(authorizationEndpoint).build();
    }

    @GetMapping("/login/oauth2/code/{vendor}")
    public ResponseEntity<Void> handleOAuth2Login(
            @PathVariable String vendor,
            @RequestParam String code
    ) {
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        URI redirectUri = oauth2Exchangeservice.handleOAuth2Login(vendor, baseUrl, code);

        return ResponseEntity.status(FOUND).location(redirectUri).build();
    }
}
