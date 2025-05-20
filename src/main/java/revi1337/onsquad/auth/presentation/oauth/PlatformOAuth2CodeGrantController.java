package revi1337.onsquad.auth.presentation.oauth;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import revi1337.onsquad.auth.application.oauth.OAuth2LoginService;
import revi1337.onsquad.auth.application.oauth.OAuth2Platform;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
import revi1337.onsquad.auth.config.properties.SupportOAuth2Platform;
import revi1337.onsquad.auth.application.oauth.model.PlatformUserProfile;
import revi1337.onsquad.auth.application.token.model.AccessToken;
import revi1337.onsquad.auth.application.token.model.JsonWebToken;
import revi1337.onsquad.common.config.properties.OnsquadProperties;

@RequiredArgsConstructor
@RestController
public class PlatformOAuth2CodeGrantController {

    private final OnsquadProperties onsquadProperties;
    private final OAuth2ClientProperties oAuth2ClientProperties;
    private final OAuth2LoginService oAuth2LoginService;

    @GetMapping("/api/login/oauth2/code/{platform}")
    public ResponseEntity<Void> receivePlatformAuthorizationCode(
            @PathVariable String platform,
            @RequestParam String code
    ) {
        OAuth2Platform oAuth2Platform = SupportOAuth2Platform.getAvailableFromSpecific(platform);
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString();
        AccessToken accessToken = oAuth2Platform.provideAccessToken(baseUrl, code, oAuth2ClientProperties);
        PlatformUserProfile userProfile = oAuth2Platform.provideUserProfile(accessToken, oAuth2ClientProperties);

        JsonWebToken jsonWebToken = oAuth2LoginService.loginOAuth2User(userProfile);
        URI redirectUri = buildRedirectUri(jsonWebToken);

        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
    }

    private URI buildRedirectUri(JsonWebToken jsonWebToken) {
        return ServletUriComponentsBuilder.fromHttpUrl(onsquadProperties.getFrontendBaseUrl())
                .queryParam("accessToken", jsonWebToken.accessToken())
                .queryParam("refreshToken", jsonWebToken.refreshToken())
                .build()
                .toUri();
    }
}
