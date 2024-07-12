package revi1337.onsquad.squad.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.auth.application.JsonWebTokenProvider;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.domain.vo.JoinStatus;
import revi1337.onsquad.factory.CrewFactory;
import revi1337.onsquad.factory.CrewMemberFactory;
import revi1337.onsquad.factory.ImageFactory;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.image.domain.Image;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.squad.dto.request.SquadCreateRequest;
import revi1337.onsquad.support.IntegrationTestSupport;

import java.util.Collections;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Squad 통합 테스트")
class SquadControllerTest extends IntegrationTestSupport {

    @Autowired private MemberRepository memberRepository;
    @Autowired private CrewRepository crewRepository;
    @Autowired private CrewMemberRepository crewMemberRepository;
    @Autowired private JsonWebTokenProvider jsonWebTokenProvider;

    @Nested
    @DisplayName("CreateNewSquad 메소드를 테스트한다.")
    class CreateNewSquad {

        private String accessToken;

        @BeforeEach
        void tearDown() {
            this.accessToken = jsonWebTokenProvider.generateAccessToken(ACCESS_TOKEN_SUBJECT, Collections.singletonMap("memberId", 1L)).value();
            this.accessToken = "Bearer " + this.accessToken;
        }

        @Transactional(propagation = Propagation.SUPPORTS)
        @DisplayName("인증된 사용자는 Squad 모집 게시글을 등록할 수 있다.")
        @Test
        public void squadTest() throws Exception {
            // given
            Member member = MemberFactory.defaultMember().build();
            Image image = ImageFactory.defaultImage();
            Crew crew = CrewFactory.defaultCrew().image(image).member(member).build();
            CrewMember crewMember = CrewMemberFactory.defaultCrewMember().member(member).crew(crew).status(JoinStatus.ACCEPT).build();
            memberRepository.save(member);
            crewRepository.save(crew);
            crewMemberRepository.save(crewMember);
            SquadCreateRequest squadCreateRequest = new SquadCreateRequest(crew.getName().getValue(), "스쿼드 제목", "스쿼드 내용", 8, "주소", "상세주소", List.of("등산"), "카카오링크", "디스코드링크");

            // when && then
            mockMvc.perform(
                            post("/api/v1/squad/new")
                                    .content(objectMapper.writeValueAsString(squadCreateRequest))
                                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value(201));
        }
    }
}