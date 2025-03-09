package revi1337.onsquad.auth.presentation.oauth2;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import revi1337.onsquad.auth.application.oauth2.OAuth2Platform;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
import revi1337.onsquad.auth.config.properties.SupportOAuth2Platform;

@RequiredArgsConstructor
@RestController
public class OAuth2RedirectionController {

    private final OAuth2ClientProperties oAuth2ClientProperties;

    @GetMapping("/api/v1/login/oauth2/{platform}")
    public ResponseEntity<String> handlePlatformOAuth2Login(@PathVariable String platform) {
        OAuth2Platform oAuth2Platform = SupportOAuth2Platform.getAvailableFromSpecific(platform);
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        URI compositeAuthorizationEndpoint = oAuth2Platform.provideUsing(baseUrl, oAuth2ClientProperties);

        return ResponseEntity.ok().location(compositeAuthorizationEndpoint).build();
    }
}
