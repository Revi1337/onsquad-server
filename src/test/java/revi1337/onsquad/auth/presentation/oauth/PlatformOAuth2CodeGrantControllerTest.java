package revi1337.onsquad.auth.presentation.oauth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import revi1337.onsquad.auth.application.oauth.OAuth2LoginService;
import revi1337.onsquad.auth.config.properties.OAuth2ClientProperties;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.common.config.properties.OnsquadProperties;

@WebMvcTest(PlatformOAuth2CodeGrantController.class)
class PlatformOAuth2CodeGrantControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private OnsquadProperties onsquadProperties;

    @MockBean
    private OAuth2ClientProperties oAuth2ClientProperties;

    @MockBean
    private OAuth2LoginService oAuth2LoginService;

    @Test
    void success() {
        String oauthPlatform = "kakao";
        String authorizationCode = "authorization-code";

//        oAuth2LoginService.

    }
}