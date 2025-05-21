package revi1337.onsquad.backup.crew.presentation;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static revi1337.onsquad.common.config.FixedTime.CLOCK;
import static revi1337.onsquad.common.fixture.CrewTopMemberFixture.CREW1_ANDONG_RANK2;
import static revi1337.onsquad.common.fixture.CrewTopMemberFixture.CREW1_REVI_RANK1;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import revi1337.onsquad.backup.crew.application.CrewTopMemberService;
import revi1337.onsquad.backup.crew.application.dto.Top5CrewMemberDto;
import revi1337.onsquad.common.PresentationLayerTestSupport;

@WebMvcTest(CrewTopMemberController.class)
class CrewTopMemberControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private CrewTopMemberService crewTopMemberService;

    @Nested
    @DisplayName("Crew 상위 랭커 조회를 문서화한다.")
    class FindTop5CrewMembers {

        @Test
        @DisplayName("Crew 상위 랭커 조회에 성공한다.")
        void success() throws Exception {
            Long DUMMY_CREW_ID = 1L;
            LocalDateTime NOW = LocalDateTime.now(CLOCK);
            Top5CrewMemberDto SERVICE_DTO1 = new Top5CrewMemberDto(
                    CREW1_REVI_RANK1.getCrewId(), CREW1_REVI_RANK1.getRanks(), CREW1_REVI_RANK1.getContribute(),
                    CREW1_REVI_RANK1.getMemberId(), CREW1_REVI_RANK1.getNickname(), CREW1_REVI_RANK1.getMbti(),
                    NOW
            );
            Top5CrewMemberDto SERVICE_DTO2 = new Top5CrewMemberDto(
                    CREW1_ANDONG_RANK2.getCrewId(), CREW1_ANDONG_RANK2.getRanks(), CREW1_ANDONG_RANK2.getContribute(),
                    CREW1_ANDONG_RANK2.getMemberId(), CREW1_ANDONG_RANK2.getNickname(), CREW1_ANDONG_RANK2.getMbti(),
                    NOW.plusHours(1)
            );
            List<Top5CrewMemberDto> SERVICE_DTOS = List.of(SERVICE_DTO1, SERVICE_DTO2);
            when(crewTopMemberService.findTop5CrewMembers(any(), eq(DUMMY_CREW_ID))).thenReturn(SERVICE_DTOS);

            mockMvc.perform(get("/api/crews/{crewId}/members/top", DUMMY_CREW_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("crew-top-member/success/fetches",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            responseBody()
                    ));

            verify(crewTopMemberService).findTop5CrewMembers(any(), eq(DUMMY_CREW_ID));
        }
    }
}