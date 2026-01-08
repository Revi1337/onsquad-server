package revi1337.onsquad.member.presentation;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_CODE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import revi1337.onsquad.auth.verification.VerificationMailService;
import revi1337.onsquad.common.PresentationLayerTestSupport;

@WebMvcTest(EmailVerificationCodeController.class)
class EmailVerificationCodeControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private VerificationMailService verificationMailService;

    @Nested
    @DisplayName("이메일 인증코드 발송을 문서화한다.")
    class SendVerificationCode {

        @Test
        @DisplayName("이메일 인증코드 발송에 성공한다.")
        void success() throws Exception {
            doNothing().when(verificationMailService).sendVerificationCode(REVI_EMAIL_VALUE);

            mockMvc.perform(post("/api/auth/send")
                            .queryParam("email", REVI_EMAIL_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(201))
                    .andDo(document("auth/success/send",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(parameterWithName("email").description("인증코드를 받을 Email")),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("이메일 인증코드 검증을 문서화한다.")
    class VerifyVerificationCode {

        @Test
        @DisplayName("이메일 인증코드 검증에 성공한다.")
        void success() throws Exception {
            when(verificationMailService.validateVerificationCode(REVI_EMAIL_VALUE, TEST_VERIFICATION_CODE))
                    .thenReturn(true);

            mockMvc.perform(get("/api/auth/verify")
                            .queryParam("email", REVI_EMAIL_VALUE)
                            .queryParam("code", TEST_VERIFICATION_CODE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("auth/success/verify",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(
                                    parameterWithName("email").description("인증코드를 받은 Email"),
                                    parameterWithName("code").description("인증코드")
                            ),
                            responseBody()
                    ));
        }
    }
}
