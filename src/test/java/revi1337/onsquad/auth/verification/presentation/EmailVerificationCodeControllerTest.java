package revi1337.onsquad.auth.verification.presentation;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import revi1337.onsquad.auth.verification.application.VerificationMailService;
import revi1337.onsquad.common.PresentationLayerTestSupport;

@WebMvcTest(EmailVerificationCodeController.class)
class EmailVerificationCodeControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private VerificationMailService verificationMailService;

    @Nested
    @DisplayName("인증 번호 전송을 문서화한다.")
    class sendVerificationCode {

        @Test
        @DisplayName("인증 번호를 이메일로 전송하는 데 성공한다.")
        void success() throws Exception {
            String email = "test@gmail.com";
            doNothing().when(verificationMailService).sendVerificationCode(anyString());

            mockMvc.perform(post("/api/auth/send")
                            .queryParam("email", email))
                    .andExpect(jsonPath("$.status").value(201))
                    .andDo(document("auth/success/verification-code/send",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(parameterWithName("email").description("인증 번호를 받을 이메일 주소")),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("인증 번호 검증을 문서화한다.")
    class verifyVerificationCode {

        @Test
        @DisplayName("인증 번호가 일치하면 true를 반환한다.")
        void success() throws Exception {
            String email = "revi1337@gmail.com";
            String code = "123456";
            when(verificationMailService.validateVerificationCode(email, code)).thenReturn(true);

            mockMvc.perform(get("/api/auth/verify")
                            .queryParam("email", email)
                            .queryParam("code", code))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("auth/success/verification-code/verify",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(
                                    parameterWithName("email").description("검증할 이메일 주소"),
                                    parameterWithName("code").description("사용자가 입력한 인증 번호")
                            ),
                            responseBody()
                    ));
        }
    }
}
