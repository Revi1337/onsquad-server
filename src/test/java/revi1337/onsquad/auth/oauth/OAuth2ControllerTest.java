package revi1337.onsquad.auth.oauth;

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
import revi1337.onsquad.auth.oauth.config.OAuth2ClientProperties;
import revi1337.onsquad.auth.oauth.config.OAuth2ClientProperties.OAuth2Properties;
import revi1337.onsquad.auth.oauth.config.SupportOAuth2Platform;
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
    private OAuth2Service oAuth2Service;

    @Nested
    @DisplayName("카카오 OAuth2 로그인 엔드포인트 생성을 문서화한다.")
    class handlePlatformOAuth2Login {

        @Test
        @DisplayName("카카오 OAuth2 로그인 엔드포인트 생성에 성공한다.")
        void success() throws Exception {
            String oauth2Platform = "kakao";
            OAuth2Properties oAuth2Properties = oAuth2ClientProperties.clients().get(SupportOAuth2Platform.KAKAO);
            String baseUrl = UriComponentsBuilder
                    .fromHttpUrl(oAuth2Properties.authorizationUri())
                    .queryParam("client_id", "CLIENT_ID")
                    .queryParam("redirect_uri", "REDIRECT_URI")
                    .queryParam("response_type", "code")
                    .build()
                    .toUriString();
            when(oAuth2Service.buildAuthorizationEndpoint(anyString(), eq(oauth2Platform)))
                    .thenReturn(URI.create(baseUrl));

            mockMvc.perform(get("/api/login/oauth2/{platform}", oauth2Platform)
                            .contentType(APPLICATION_JSON))
                    .andDo(document("auth/success/kakao-endpoint",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("platform").description("OAuth 인증 Platform (현재 kakao 만 존재")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("카카오 OAuth2 로그인을 문서화한다.")
    class handleOAuth2Login {

        @Test
        @DisplayName("카카오 OAuth2 로그인을 문서화에 성공한다.")
        void success() throws Exception {
            String oauth2Platform = "kakao";
            String authorizationCode = "authorization-code";
            String redirectUri = UriComponentsBuilder
                    .fromHttpUrl(onsquadProperties.getFrontendBaseUrl())
                    .queryParam("accessToken", ACCESS_TOKEN)
                    .queryParam("refreshToken", REFRESH_TOKEN)
                    .build()
                    .toUriString();
            when(oAuth2Service.handleOAuth2Login(anyString(), eq(oauth2Platform), eq(authorizationCode)))
                    .thenReturn(URI.create(redirectUri));

            mockMvc.perform(get("/api/login/oauth2/code/{platform}", oauth2Platform)
                            .queryParam("code", authorizationCode)
                            .contentType(APPLICATION_JSON))
                    .andDo(document("auth/success/kakao-login",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(
                                    parameterWithName("platform").description("OAuth 인증 Platform (현재 kakao 만 존재")
                            ),
                            queryParameters(
                                    parameterWithName("code").description("인가코드")
                            ),
                            responseBody()
                    ));
        }
    }
}
