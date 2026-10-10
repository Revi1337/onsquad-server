package revi1337.onsquad.auth.oauth.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.LOCATION;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import revi1337.onsquad.common.config.etc.YamlPropertySourceFactory;
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
            when(OAuth2ExchangeService.buildAuthorizationEndpoint(eq(OAuth2Vendor.KAKAO), anyString()))
                    .thenReturn(URI.create(baseUrl));

            mockMvc.perform(get("/api/login/oauth2/{vendor}", oAuth2Vendor)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(header().string(LOCATION, baseUrl))
                    .andDo(document("auth/success/oauth2-endpoint",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(parameterWithName("vendor").description("OAuth 인증 Vendor")),
                            responseBody()
                    ));

            verify(OAuth2ExchangeService, times(1)).buildAuthorizationEndpoint(eq(OAuth2Vendor.KAKAO), anyString());
        }

        @Test
        @DisplayName("벤더는 대소문자를 구분하지 않는다.")
        void success_whenVendorIsUpperCase() throws Exception {
            URI endpoint = URI.create("https://kauth.kakao.com/oauth/authorize");
            when(OAuth2ExchangeService.buildAuthorizationEndpoint(eq(OAuth2Vendor.KAKAO), anyString()))
                    .thenReturn(endpoint);

            mockMvc.perform(get("/api/login/oauth2/{vendor}", "KAKAO"))
                    .andExpect(status().isOk())
                    .andExpect(header().string(LOCATION, endpoint.toString()));
        }

        @Test
        @DisplayName("지원하지 않는 벤더면 서비스를 호출하지 않고 400 과 C004 로 응답한다.")
        void fail_whenVendorIsNotSupported() throws Exception {
            mockMvc.perform(get("/api/login/oauth2/{vendor}", "naver"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error.code").value("C004"));

            verify(OAuth2ExchangeService, never()).buildAuthorizationEndpoint(any(), anyString());
        }

        @Test
        @DisplayName("벤더 자리에 code 가 오면 지원하지 않는 벤더로 400 과 C004 로 응답한다.")
        void fail_whenVendorIsCodeSegment() throws Exception {
            mockMvc.perform(get("/api/login/oauth2/code"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.code").value("C004"));
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
            when(OAuth2ExchangeService.handleOAuth2Login(eq(OAuth2Vendor.KAKAO), anyString(), eq(authorizationCode)))
                    .thenReturn(URI.create(redirectUri));

            mockMvc.perform(get("/api/login/oauth2/code/{vendor}", oauth2Vendor)
                            .queryParam("code", authorizationCode)
                            .contentType(APPLICATION_JSON))
                    .andExpect(status().isFound())
                    .andExpect(header().string(LOCATION, redirectUri))
                    .andDo(document("auth/success/oauth2-login",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(parameterWithName("vendor").description("OAuth 인증 Vendor")),
                            queryParameters(parameterWithName("code").description("인가코드")),
                            responseBody()
                    ));
        }

        @Test
        @DisplayName("지원하지 않는 벤더면 서비스를 호출하지 않고 400 과 C004 로 응답한다.")
        void fail_whenVendorIsNotSupported() throws Exception {
            mockMvc.perform(get("/api/login/oauth2/code/{vendor}", "naver")
                            .queryParam("code", "authorization-code"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error.code").value("C004"));

            verify(OAuth2ExchangeService, never()).handleOAuth2Login(any(), anyString(), anyString());
        }
    }
}
