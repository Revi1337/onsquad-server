package revi1337.onsquad.crew_member.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.crew_member.application.CrewMemberService;
import revi1337.onsquad.crew_member.application.dto.CrewMemberDto;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;

@WebMvcTest(CrewMemberController.class)
class CrewMemberControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private CrewMemberService crewMemberService;

    @Nested
    @DisplayName("Crew 에 속한 CrewMember 관리 목록 조회를 문서화한다.")
    class FetchCrewMembers {

        @Test
        @DisplayName("Crew 에 속한 CrewMember 관리 목록 조회에 성공한다.")
        void success() throws Exception {
            Long DUMMY_CREW_ID = 1L;
            PageRequest PAGE_REQUEST = PageRequest.of(0, 5);
            LocalDateTime NOW = LocalDateTime.now();
            CrewMemberDto SERVICE_DTO1 = new CrewMemberDto(
                    NOW.plusDays(1),
                    new SimpleMemberInfoDto(1L, null, REVI_NICKNAME_VALUE, REVI_MBTI_VALUE)
            );
            CrewMemberDto SERVICE_DTO2 = new CrewMemberDto(
                    NOW,
                    new SimpleMemberInfoDto(2L, null, ANDONG_NICKNAME_VALUE, ANDONG_MBTI_VALUE)
            );
            List<CrewMemberDto> SERVICE_DTOS = List.of(SERVICE_DTO1, SERVICE_DTO2);
            Page<CrewMemberDto> PAGE_DTOS = new PageImpl<>(SERVICE_DTOS, PAGE_REQUEST, SERVICE_DTOS.size());
            when(crewMemberService.fetchCrewMembers(any(), eq(DUMMY_CREW_ID), eq(PAGE_REQUEST))).thenReturn(PAGE_DTOS);

            mockMvc.perform(get("/api/crews/{crewId}/manage/members", DUMMY_CREW_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .param("page", "0")
                            .param("size", "5")
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("crew-member/success/fetches",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            queryParameters(
                                    parameterWithName("page").description("페이지").optional(),
                                    parameterWithName("size").description("페이지 당 사이즈").optional()
                            ),
                            responseBody()
                    ));

            verify(crewMemberService).fetchCrewMembers(any(), eq(DUMMY_CREW_ID), eq(PAGE_REQUEST));
        }
    }
}
