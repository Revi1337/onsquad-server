package revi1337.onsquad.member.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import revi1337.onsquad.member.application.MemberJoinService;
import revi1337.onsquad.member.dto.request.MemberJoinRequest;
import revi1337.onsquad.support.ValidationWithRestDocsTestSupport;

import java.util.stream.Stream;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("회원가입 api Validation 테스트")
@WebMvcTest(MemberJoinController.class)
class MemberTestJoinControllerValidationTest extends ValidationWithRestDocsTestSupport {

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_PASSWORD = "12345!@asa";
    private static final String TEST_AUTH_CODE = "1111";
    private static final String TEST_NICKNAME = "nickname";

    @MockBean private MemberJoinService memberJoinService;

    @DisplayName("인증코드를 발송한다.")
    @Nested
    class SendAuthCodeToEmail {

        @DisplayName("입력값 검증에 실패하면 인증코드를 발송할 수 없다.")
        @MethodSource("parameterizedSendAuthCodeToEmail")
        @ParameterizedTest(name = "[{index}]. Argument ({arguments}) {displayName}")
        public void sendAuthCodeToEmail(String email) throws Exception {
            // given & when & then
            mockMvc.perform(
                            get("/api/v1/auth/send")
                                    .queryParam("email", email)
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest());
        }

        static Stream<Arguments> parameterizedSendAuthCodeToEmail() {
            return Stream.of(
                    Arguments.of(""),
                    Arguments.of("invalid_email")
            );
        }

        @DisplayName("인증코드 발송 문서를 작성한다.")
        @Test
        public void sendAuthCodeToEmailDocsTest() throws Exception {
            // given
            willDoNothing().given(memberJoinService).sendAuthCodeToEmail(TEST_EMAIL);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/auth/send")
                            .queryParam("email", TEST_EMAIL)
                            .contentType(APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "member-join-controller/sendEmail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("email").description("이메일")
                                    )
                            )
                    );
        }
    }

    @DisplayName("인증코드를 검증한다.")
    @Nested
    class VerifyAuthCode {

        @DisplayName("입력값 검증에 실패하면 인증코드를 검증할 수 없다.")
        @MethodSource("parameterizedVerifyAuthCodeArguments")
        @ParameterizedTest(name = "[{index}]. Argument ({0} & {1}) {displayName}")
        public void verifyAuthCode(String email, String authCode) throws Exception {
            // given & when & then
            mockMvc.perform(
                            get("/api/v1/auth/valid")
                                    .queryParam("email", email)
                                    .queryParam("authCode", authCode)
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest());
        }

        static Stream<Arguments> parameterizedVerifyAuthCodeArguments() {
            String testEmail = "malformed_email";
            String testAuthCode = "malformed_authCode";
            return Stream.of(
                    Arguments.of("", testAuthCode),
                    Arguments.of(testEmail, ""),
                    Arguments.of("", "")
            );
        }

        @DisplayName("[Docs] 인증코드 검증 문서를 작성한다.")
        @Test
        public void verifyAuthCodeDocsTest() throws Exception {
            // given
            given(memberJoinService.verifyAuthCode(TEST_EMAIL, TEST_AUTH_CODE)).willReturn(true);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/auth/valid")
                            .queryParam("email", TEST_EMAIL)
                            .queryParam("authCode", TEST_AUTH_CODE)
                            .contentType(APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.error").doesNotExist())
                    .andExpect(jsonPath("$.data.valid").value(true))
                    .andDo(
                            document(
                                    "member-join-controller/verifyEmail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("email").description("이메일"),
                                            parameterWithName("authCode").description("이메일 인증코드")
                                    )
                            )
                    );
        }
    }

    @DisplayName("닉네임 중복검사를 진행한다.")
    @Nested
    class CheckDuplicateNickname {

        @DisplayName("입력값 검증에 실패하면 닉네임 중복검사를 진행할 수 없다.")
        @Test
        public void checkDuplicateNickname() throws Exception {
            // given
            String nickname = "";

            // when & then
            mockMvc.perform(
                            get("/api/v1/auth/check")
                                    .queryParam("nickname", nickname)
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("[Docs] 닉네임 중복 검사 문서를 작성한다.")
        @Test
        public void checkDuplicateNicknameDocsTest() throws Exception {
            // given
            given(memberJoinService.checkDuplicateNickname(TEST_NICKNAME)).willReturn(false);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/auth/check")
                            .queryParam("nickname", TEST_NICKNAME)
                            .contentType(APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.duplicate").value(false))
                    .andDo(
                            document(
                                    "member-join-controller/duplicateEmail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("nickname").description("닉네임")
                                    )
                            )
                    );
        }
    }

    @DisplayName("회원가입을 진행한다.")
    @Nested
    class JoinMember {

        @DisplayName("입력값의 검증에 실패하면 회원가입을 진행할 수 없다.")
        @MethodSource("parameterizedJoinMemberArguments")
        @ParameterizedTest(name = "[{index}]. {displayName}")
        public void joinMember5(MemberJoinRequest memberJoinRequest) throws Exception {
            // given & when & then
            mockMvc.perform(
                            post("/api/v1/auth/join")
                                    .content(objectMapper.writeValueAsString(memberJoinRequest))
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest());
        }

        static Stream<Arguments> parameterizedJoinMemberArguments() {
            return Stream.of(
                    Arguments.of(new MemberJoinRequest("", "password", "password", "nickname", "anywhere")),
                    Arguments.of(new MemberJoinRequest("test@mail.com", "", "password", "nickname", "anywhere")),
                    Arguments.of(new MemberJoinRequest("test@mail.com", "password", "", "nickname", "anywhere")),
                    Arguments.of(new MemberJoinRequest("test@mail.com", "password", "password", "", "anywhere")),
                    Arguments.of(new MemberJoinRequest("test@mail.com", "password", "password", "nickname", ""))
            );
        }

        @DisplayName("[Docs] 회원가입 문서를 작성한다.")
        @Test
        public void joinMemberDocTest() throws Exception {
            // given
            MemberJoinRequest memberJoinRequest = new MemberJoinRequest(
                    TEST_EMAIL, TEST_PASSWORD, TEST_PASSWORD, "nickname", "어딘가"
            );
            willDoNothing().given(memberJoinService).joinMember(memberJoinRequest.toDto());

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/auth/join")
                            .content(objectMapper.writeValueAsString(memberJoinRequest))
                            .contentType(APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isCreated())
                    .andDo(
                            document(
                                    "member-join-controller/joinMember",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                            fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                                            fieldWithPath("passwordConfirm").type(JsonFieldType.STRING).description("비밀번호 확인"),
                                            fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                            fieldWithPath("address").type(JsonFieldType.STRING).description("주소")
                                    )
                            )
                    );
        }
    }
}