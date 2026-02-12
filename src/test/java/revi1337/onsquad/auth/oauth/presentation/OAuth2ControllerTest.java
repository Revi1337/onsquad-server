package revi1337.onsquad.auth.oauth.presentation;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;

import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.util.UriComponentsBuilder;
import revi1337.onsquad.auth.oauth.application.OAuth2ExchangeService;
import revi1337.onsquad.auth.oauth.application.OAuth2Vendor;
import revi1337.onsquad.auth.oauth.infrastructure.OAuth2ClientProperties;
import revi1337.onsquad.auth.oauth.infrastructure.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.common.config.YamlPropertySourceFactory;
import revi1337.onsquad.common.config.system.properties.OnsquadProperties;

@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
@EnableConfigurationProperties({OAuth2ClientProperties.class, OnsquadProperties.class})
@WebMvcTest(OAuth2Controller.class)
class OAuth2ControllerTest extends PresentationLayerTestSupport {

    @Autowired
    private OnsquadProperties onsquadProperties;

    @Autowired
    private OAuth2ClientProperties oAuth2ClientProperties;

    @MockBean
    private OAuth2ExchangeService OAuth2ExchangeService;

    @Nested
    @DisplayName("OAuth2 로그인 엔드포인트 생성을 문서화한다.")
    class handleOAuth2VendorLogin {

        @Test
        @DisplayName("OAuth2 로그인 엔드포인트 생성에 성공한다.")
        void success() throws Exception {
            String oAuth2Vendor = "kakao";
            OAuth2Properties oAuth2Properties = oAuth2ClientProperties.clients().get(OAuth2Vendor.KAKAO);
            String baseUrl = UriComponentsBuilder
                    .fromHttpUrl(oAuth2Properties.authorizationUri())
                    .queryParam("client_id", "CLIENT_ID")
                    .queryParam("redirect_uri", "REDIRECT_URI")
                    .queryParam("response_type", "code")
                    .build()
                    .toUriString();
            when(OAuth2ExchangeService.buildAuthorizationEndpoint(anyString(), eq(oAuth2Vendor)))
                    .thenReturn(URI.create(baseUrl));

            mockMvc.perform(get("/api/login/oauth2/{vendor}", oAuth2Vendor)
                            .contentType(APPLICATION_JSON))
                    .andDo(document("auth/success/oauth2-endpoint",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(parameterWithName("vendor").description("OAuth 인증 Vendor")),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("OAuth2 로그인을 문서화한다.")
    class handleOAuth2VLogin {

        @Test
        @DisplayName("OAuth2 로그인을 문서화에 성공한다.")
        void success() throws Exception {
            String oauth2Vendor = "kakao";
            String authorizationCode = "authorization-code";
            String redirectUri = UriComponentsBuilder
                    .fromHttpUrl(onsquadProperties.getFrontendBaseUrl())
                    .queryParam("accessToken", ACCESS_TOKEN)
                    .queryParam("refreshToken", REFRESH_TOKEN)
                    .build()
                    .toUriString();
            when(OAuth2ExchangeService.handleOAuth2Login(anyString(), eq(oauth2Vendor), eq(authorizationCode)))
                    .thenReturn(URI.create(redirectUri));

            mockMvc.perform(get("/api/login/oauth2/code/{vendor}", oauth2Vendor)
                            .queryParam("code", authorizationCode)
                            .contentType(APPLICATION_JSON))
                    .andDo(document("auth/success/oauth2-login",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(parameterWithName("vendor").description("OAuth 인증 Vendor")),
                            queryParameters(parameterWithName("code").description("인가코드")),
                            responseBody()
                    ));
        }
    }
}
