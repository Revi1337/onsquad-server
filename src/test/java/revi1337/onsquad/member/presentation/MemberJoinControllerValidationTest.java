package revi1337.onsquad.member.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import revi1337.onsquad.member.application.MemberJoinService;
import revi1337.onsquad.member.presentation.request.MemberJoinRequest;
import revi1337.onsquad.support.ValidationTestSupport;

import java.util.stream.Stream;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("회원가입 api Validation 테스트")
@WebMvcTest(MemberJoinController.class)
class MemberJoinControllerValidationTest extends ValidationTestSupport {

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
    }
}