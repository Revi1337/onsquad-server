package revi1337.onsquad.auth.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_PASSWORD_VALUE;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import revi1337.onsquad.auth.application.CurrentMember;
import revi1337.onsquad.auth.application.token.JsonWebTokenManager;
import revi1337.onsquad.auth.application.token.dto.LoginRequest;
import revi1337.onsquad.auth.application.token.model.AccessToken;
import revi1337.onsquad.auth.application.token.model.RefreshToken;
import revi1337.onsquad.auth.application.token.security.JsonWebTokenAuthenticationProvider;
import revi1337.onsquad.auth.application.token.security.JsonWebTokenFailureHandler;
import revi1337.onsquad.auth.application.token.security.JsonWebTokenLoginFilter;
import revi1337.onsquad.auth.application.token.security.JsonWebTokenSuccessHandler;
import revi1337.onsquad.auth.application.token.security.JsonWebTokenUserDetailsService;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.member.domain.vo.UserType;

@WebMvcTest(JsonWebTokenLoginFilter.class)
class JsonWebTokenLoginFilterTest extends PresentationLayerTestSupport {

    @MockBean
    private JsonWebTokenManager jsonWebTokenManager;

    @MockBean
    private JsonWebTokenUserDetailsService jsonWebTokenUserDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    protected void setUp(
            final WebApplicationContext applicationContext,
            final RestDocumentationContextProvider restDocumentationProvider
    ) {
        JsonWebTokenAuthenticationProvider provider = new JsonWebTokenAuthenticationProvider(
                passwordEncoder, jsonWebTokenUserDetailsService
        );
        AuthenticationManager authenticationManager = new ProviderManager(provider);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        JsonWebTokenFailureHandler failureHandler = new JsonWebTokenFailureHandler();
        HandlerExceptionResolver exceptionResolver = applicationContext
                .getBean("handlerExceptionResolver", HandlerExceptionResolver.class);
        ReflectionTestUtils.setField(failureHandler, "handlerExceptionResolver", exceptionResolver);

        JsonWebTokenLoginFilter filter = new JsonWebTokenLoginFilter(
                authenticationManager, objectMapper, validator
        );
        filter.setAuthenticationSuccessHandler(new JsonWebTokenSuccessHandler(jsonWebTokenManager, objectMapper));
        filter.setAuthenticationFailureHandler(failureHandler);

        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentationProvider))
                .alwaysDo(MockMvcResultHandlers.print())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .addFilter(filter)
                .build();
    }

    @Test
    @DisplayName("로그인에 성공을 문서화한다.")
    void success() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest(REVI_EMAIL_VALUE, REVI_PASSWORD_VALUE);
        CurrentMember currentMember = new CurrentMember(
                1L,
                REVI_EMAIL_VALUE,
                REVI_PASSWORD_VALUE,
                UserType.GENERAL,
                Set.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(jsonWebTokenUserDetailsService.loadUserByUsername(REVI_EMAIL_VALUE)).thenReturn(currentMember);
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jsonWebTokenManager.generateAccessToken(any())).thenReturn(new AccessToken(ACCESS_TOKEN));
        when(jsonWebTokenManager.generateRefreshToken(any())).thenReturn(new RefreshToken(REFRESH_TOKEN));

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(document("auth/success/login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseBody()
                ));
    }

    @Test
    @DisplayName("로그인 실패를 문서화한다.")
    void fail() throws Exception {
        // given
        LoginRequest loginRequest = new LoginRequest(REVI_EMAIL_VALUE, REVI_PASSWORD_VALUE);
        CurrentMember currentMember = new CurrentMember(
                1L,
                REVI_EMAIL_VALUE,
                REVI_PASSWORD_VALUE,
                UserType.GENERAL,
                Set.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(jsonWebTokenUserDetailsService.loadUserByUsername(REVI_EMAIL_VALUE)).thenReturn(currentMember);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andDo(document("auth/fail/login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseBody()
                ));
    }
}
