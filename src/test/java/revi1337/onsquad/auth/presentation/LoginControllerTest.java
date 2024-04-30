package revi1337.onsquad.auth.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import revi1337.onsquad.auth.dto.request.LoginRequest;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Password;
import revi1337.onsquad.support.IntegrationTestSupport;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("로그인 api 테스트")
public class LoginControllerTest extends IntegrationTestSupport {

    @Autowired private MemberRepository memberRepository;

    @DisplayName("로그인하면 AccessToken 과 RefreshToken 이 내려온다.")
    @Test
    public void loginTest() throws Exception {
        // given
        Member member = MemberFactory.defaultMember().email(new Email(TEST_EMAIL)).password(new Password(TEST_BCRYPT_PASSWORD)).build();
        memberRepository.save(member);
        LoginRequest loginRequest = LoginRequest.of(TEST_EMAIL, TEST_PASSWORD);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/v1/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(APPLICATION_JSON)
        );

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken.value").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken.value").isNotEmpty())
                .andDo(
                        document(
                                "login-controller/login",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                                )
                        )
                );
    }
}
