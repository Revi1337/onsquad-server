package revi1337.onsquad.squad.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MvcResult;
import revi1337.onsquad.auth.dto.request.LoginRequest;
import revi1337.onsquad.auth.dto.response.JsonWebTokenResponse;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Password;
import revi1337.onsquad.squad.application.SquadService;
import revi1337.onsquad.squad.dto.request.SquadCreateRequest;
import revi1337.onsquad.support.IntegrationTestSupport;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Squad 통합 테스트")
class SquadControllerTest extends IntegrationTestSupport {

    @Autowired private MemberRepository memberRepository;
    @Autowired private SquadService squadService;

    @Rollback(false)
    @DisplayName("인증된 사용자는 Squad 모집 게시글을 등록할 수 있다.")
    @Test
    public void squadTest() throws Exception {
        // given
        Member member = MemberFactory.defaultMember().email(new Email(TEST_EMAIL)).password(new Password(TEST_BCRYPT_PASSWORD)).build();
        memberRepository.save(member);
        LoginRequest loginRequest = LoginRequest.of(TEST_EMAIL, TEST_PASSWORD);
        MvcResult mvcResult = mockMvc.perform(
                post("/api/v1/auth/login")
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(APPLICATION_JSON)
        ).andReturn();
        JsonWebTokenResponse jsonWebTokenResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), JsonWebTokenResponse.class);
        SquadCreateRequest squadCreateRequest = SquadCreateRequest.of(List.of("등산"), "서울시 강서구", "우장산 롯데캐슬", 10, "제목", "내용", "카카오톡링크", "디스코드 링크");

        // when && then
        mockMvc.perform(
                post("/api/v1/squad/new")
                        .content(objectMapper.writeValueAsString(squadCreateRequest))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jsonWebTokenResponse.accessToken().value())
                        .contentType(APPLICATION_JSON)
        )
                .andExpect(status().isCreated());
    }
}