package revi1337.onsquad.member.presentation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.ResultActions;
import revi1337.onsquad.common.mail.MailStatus;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.member.application.MemberJoinService;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.member.domain.redis.RedisMailRepository;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.dto.request.MemberJoinRequest;
import revi1337.onsquad.support.IntegrationTestSupport;

import java.time.Duration;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("회원가입 api 테스트")
class MemberJoinControllerTest extends IntegrationTestSupport {

    @SpyBean private MemberJoinService memberJoinService;
    @Autowired private RedisMailRepository redisMailRepository;
    @Autowired private MemberJpaRepository memberRepository;

    @DisplayName("인증코드 발송을 진행한다.")
    @Nested
    class SendAuthCodeToEmail {

        @DisplayName("인증코드 발송이 정상적으로 동작하는지 확인한다.")
        @Test
        public void sendAuthCodeToEmail() throws Exception {
            // given
            String testEmail = "david122123@gmail.com";
            willDoNothing().given(memberJoinService).sendAuthCodeToEmail(testEmail);

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/auth/send")
                            .queryParam("email", testEmail)
                            .contentType(APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isOk());
            then(memberJoinService).should(times(1)).sendAuthCodeToEmail(testEmail);
        }
    }

    @DisplayName("인증코드 검증을 진행한다.")
    @Nested
    class VerifyAuthCode {

        @DisplayName("인증코드 검증이 성공하면 true 를 반환한다.")
        @Test
        public void verifyAuthCode() throws Exception {
            // given
            redisMailRepository.saveAuthCode(TEST_EMAIL, TEST_AUTH_CODE, Duration.ofMinutes(5));

            // when & then
            mockMvc.perform(
                            get("/api/v1/auth/valid")
                                    .queryParam("email", TEST_EMAIL)
                                    .queryParam("authCode", TEST_AUTH_CODE)
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.error").doesNotExist())
                    .andExpect(jsonPath("$.data.valid").value(true));
        }

        @DisplayName("인증코드 검증에 실패하면 false 를 반환한다.")
        @Test
        public void verifyAuthCode2() throws Exception {
            // given
            redisMailRepository.saveAuthCode("another_email", TEST_AUTH_CODE, Duration.ofMinutes(5));

            // when & then
            mockMvc.perform(
                            get("/api/v1/auth/valid")
                                    .queryParam("email", TEST_EMAIL)
                                    .queryParam("authCode", TEST_AUTH_CODE)
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.error").doesNotExist())
                    .andExpect(jsonPath("$.data.valid").value(false));
        }
    }

    @DisplayName("닉네임 중복검사를 진행한다.")
    @Nested
    class CheckDuplicateNickname {

        @AfterEach
        void tearDown() {
            memberRepository.deleteAllInBatch();
        }

        @DisplayName("닉네임 중복이 확인되면 true 를 반환한다.")
        @Test
        public void checkDuplicateNickname() throws Exception {
            // given
            Member member = MemberFactory.defaultMember().build();
            memberRepository.save(member);

            // when & then
            mockMvc.perform(
                            get("/api/v1/auth/check")
                                    .queryParam("nickname", "nickname")
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.duplicate").value(true));
        }

        @DisplayName("닉네임 중복이 확인되지 않으면 false 를 반환한다.")
        @Test
        public void checkDuplicateNickname2() throws Exception {
            // given
            String nickname = TEST_NICKNAME;

            // when & then
            mockMvc.perform(
                            get("/api/v1/auth/check")
                                    .queryParam("nickname", nickname)
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.duplicate").value(false));
        }
    }

    @DisplayName("회원가입을 진행한다")
    @Nested
    class JoinMember {

        @AfterEach
        void tearDown() {
            memberRepository.deleteAllInBatch();
        }

        @DisplayName("메일인증이 진행되어있으면 회원가입에 성공한다.")
        @Test
        public void joinMember() throws Exception {
            // given
            redisMailRepository.saveAuthCode(TEST_EMAIL, TEST_AUTH_CODE, Duration.ofMinutes(5));
            redisMailRepository.overwriteAuthCodeToStatus(TEST_EMAIL, MailStatus.SUCCESS, Duration.ofMinutes(5));
            MemberJoinRequest memberJoinRequest = new MemberJoinRequest(
                    TEST_EMAIL, TEST_PASSWORD, TEST_PASSWORD, "nickname", "어딘가", "우장산 롯데캐슬"
            );

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/join")
                                    .content(objectMapper.writeValueAsString(memberJoinRequest))
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(201));
        }

        @DisplayName("메일인증이 진행되어있지 않으면 회원가입에 실패한다.")
        @Test
        public void joinMember2() throws Exception {
            // given
            redisMailRepository.saveAuthCode(TEST_EMAIL, TEST_AUTH_CODE, Duration.ofMinutes(5));
            MemberJoinRequest memberJoinRequest = new MemberJoinRequest(
                    TEST_EMAIL, TEST_PASSWORD, TEST_PASSWORD, "nickname", "어딘가", "우장산 롯데캐슬"
            );

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/join")
                                    .content(objectMapper.writeValueAsString(memberJoinRequest))
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.message").value("메일 인증이 되어있지 않습니다."));
        }

        @DisplayName("닉네임이 중복되면 회원가입에 실패한다.")
        @Test
        public void joinMember3() throws Exception {
            // given
            Member member = MemberFactory.withNickname(new Nickname(TEST_NICKNAME));
            memberRepository.save(member);
            redisMailRepository.saveAuthCode(TEST_EMAIL, TEST_AUTH_CODE, Duration.ofMinutes(5));
            MemberJoinRequest memberJoinRequest = new MemberJoinRequest(
                    TEST_EMAIL, TEST_PASSWORD, TEST_PASSWORD, TEST_NICKNAME, "어딘가", "우장산 롯데캐슬"
            );

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/join")
                                    .content(objectMapper.writeValueAsString(memberJoinRequest))
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.message").value(String.format("%s 닉네임은 이미 사용중입니다.", member.getNickname().getValue())));
        }

        @DisplayName("이메일이 중복되면 회원가입에 실패한다.")
        @Test
        public void joinMember4() throws Exception {
            // given
            redisMailRepository.saveAuthCode(TEST_EMAIL, TEST_AUTH_CODE, Duration.ofMinutes(5));
            redisMailRepository.overwriteAuthCodeToStatus(TEST_EMAIL, MailStatus.SUCCESS, Duration.ofMinutes(5));
            memberRepository.save(MemberFactory.withEmail(new Email(TEST_EMAIL)));
            MemberJoinRequest memberJoinRequest = new MemberJoinRequest(
                    TEST_EMAIL, TEST_PASSWORD, TEST_PASSWORD, "nick", "어딘가", "우장산 롯데캐슬"
            );

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/join")
                                    .content(objectMapper.writeValueAsString(memberJoinRequest))
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.message").value("이미 회원가입이 되어있는 사용자입니다."));
        }

        @DisplayName("이메일이 형식이 옳지 않으면 회원가입에 실패한다.")
        @Test
        public void joinMember5() throws Exception {
            // given
            MemberJoinRequest memberJoinRequest = new MemberJoinRequest(
                    "invalid_email", TEST_PASSWORD, TEST_PASSWORD, "nickname", "anywhere", "우장산 롯데캐슬"
            );

            // when && then
            mockMvc.perform(
                            post("/api/v1/auth/join")
                                    .content(objectMapper.writeValueAsString(memberJoinRequest))
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.message").value("이메일 형식이 올바르지 않습니다."));
        }

        @DisplayName("닉네임 길이가 올바르지 않으면 회원가입에 실패한다.")
        @Test
        public void joinMember6() throws Exception {
            // given
            MemberJoinRequest memberJoinRequest = new MemberJoinRequest(
                    TEST_EMAIL, TEST_PASSWORD, TEST_PASSWORD, "a", "anywhere", "우장산 롯데캐슬"
            );

            // when && then
            mockMvc.perform(
                            post("/api/v1/auth/join")
                                    .content(objectMapper.writeValueAsString(memberJoinRequest))
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.message").value("닉네임은 2 자 이상 8 자 이하여야합니다."));
        }
    }
}
