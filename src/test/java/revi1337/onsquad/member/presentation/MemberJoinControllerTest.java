package revi1337.onsquad.member.presentation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.mail.MailStatus;
import revi1337.onsquad.config.SpringActiveProfilesResolver;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.member.application.MemberJoinService;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.redis.RedisMailRepository;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.dto.request.MemberJoinRequest;
import revi1337.onsquad.support.RestDocumentationWithRedisSupport;

import java.time.Duration;

import static org.mockito.BDDMockito.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("회원가입 api 테스트")
@SpringBootTest
@Transactional
@ActiveProfiles(resolver = SpringActiveProfilesResolver.class)
class MemberJoinControllerTest extends RestDocumentationWithRedisSupport {

    @SpyBean private MemberJoinService memberJoinService;
    @Autowired private RedisMailRepository redisMailRepository;
    @Autowired private MemberRepository memberRepository;

    private static final String TEST_EMAIL = "test@email.com";
    private static final String TEST_AUTH_CODE = "1111";

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
            verify(memberJoinService, times(1)).sendAuthCodeToEmail(testEmail);
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

    @DisplayName("인증코드 검증을 진행한다.")
    @Nested
    class VerifyAuthCode {

        @DisplayName("인증코드 검증이 성공하면 true 를 반환한다.")
        @Test
        public void verifyAuthCode() throws Exception {
            // given
            redisMailRepository.saveAuthCode(TEST_EMAIL, TEST_AUTH_CODE, Duration.ofMinutes(5));

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

            // when
            ResultActions resultActions = mockMvc.perform(
                    get("/api/v1/auth/check")
                            .queryParam("nickname", "nickname")
                            .contentType(APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.duplicate").value(true))
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

        @DisplayName("닉네임 중복이 확인되지 않으면 false 를 반환한다.")
        @Test
        public void checkDuplicateNickname2() throws Exception {
            // given
            String nickname = "nickname";

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
                    TEST_EMAIL, "password", "password", "nickname", "어딘가"
            );

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

        @DisplayName("메일인증이 진행되어있지 않으면 회원가입에 실패한다.")
        @Test
        public void joinMember2() throws Exception {
            // given
            redisMailRepository.saveAuthCode(TEST_EMAIL, TEST_AUTH_CODE, Duration.ofMinutes(5));
            MemberJoinRequest memberJoinRequest = new MemberJoinRequest(
                    TEST_EMAIL, "password", "password", "nickname", "어딘가"
            );

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/join")
                                    .content(objectMapper.writeValueAsString(memberJoinRequest))
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value("M001"))
                    .andExpect(jsonPath("$.error.message").value("메일 인증이 되어있지 않은 상태"));
        }

        @DisplayName("닉네임이 중복되면 회원가입에 실패한다.")
        @Test
        public void joinMember3() throws Exception {
            // given
            String nickname = "nickname";
            Member member = MemberFactory.withNickname(new Nickname(nickname));
            memberRepository.save(member);
            redisMailRepository.saveAuthCode(TEST_EMAIL, TEST_AUTH_CODE, Duration.ofMinutes(5));
            MemberJoinRequest memberJoinRequest = new MemberJoinRequest(
                    TEST_EMAIL, "password", "password", nickname, "어딘가"
            );

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/join")
                                    .content(objectMapper.writeValueAsString(memberJoinRequest))
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value("M002"))
                    .andExpect(jsonPath("$.error.message").value("닉네임이 중복된 상태"));
        }

        @DisplayName("닉네임이 중복되지 않지만 메일인증이 되어있지 않으면 회원가입에 실패한다.")
        @Test
        public void joinMember4() throws Exception {
            // given
            String nickname = "nickname";
            Member member = MemberFactory.withNickname(new Nickname(nickname));
            memberRepository.save(member);
            redisMailRepository.saveAuthCode(TEST_EMAIL, TEST_AUTH_CODE, Duration.ofMinutes(5));
            MemberJoinRequest memberJoinRequest = new MemberJoinRequest(
                    TEST_EMAIL, "password", "password", "nick", "어딘가"
            );

            // when & then
            mockMvc.perform(
                            post("/api/v1/auth/join")
                                    .content(objectMapper.writeValueAsString(memberJoinRequest))
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value("M001"))
                    .andExpect(jsonPath("$.error.message").value("메일 인증이 되어있지 않은 상태"));
        }

        @DisplayName("이메일이 형식이 옳지 않으면 예외를 던진다.")
        @Test
        public void joinMember5() throws Exception {
            // given
            MemberJoinRequest memberJoinRequest = new MemberJoinRequest(
                    "invalid_email", "password", "password", "nickname", "anywhere"
            );

            // when && then
            mockMvc.perform(
                            post("/api/v1/auth/join")
                                    .content(objectMapper.writeValueAsString(memberJoinRequest))
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value("M003"))
                    .andExpect(jsonPath("$.error.message").value("이메일 형식이 올바르지 않은 상태"));
        }
    }
}

