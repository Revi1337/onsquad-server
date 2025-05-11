package revi1337.onsquad.squad_participant.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad.application.dto.SquadAcceptDto;
import revi1337.onsquad.squad.presentation.dto.request.SquadAcceptRequest;
import revi1337.onsquad.squad_participant.application.SquadParticipantCommandService;
import revi1337.onsquad.squad_participant.application.SquadParticipantQueryService;
import revi1337.onsquad.squad_participant.application.dto.SimpleSquadParticipantDto;

@WebMvcTest(SquadParticipantController.class)
class SquadParticipantControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private SquadParticipantCommandService squadParticipantCommandService;

    @MockBean
    private SquadParticipantQueryService squadParticipantQueryService;

    @Nested
    @DisplayName("내가 신청한 스쿼드 신청들 조회를 문서화한다.")
    class FetchMy {

        @Test // TODO Presentation, Application, Persistence 테스트 보류. 페이징 나뉠 가능성이 매우 큼
        @DisplayName("내가 신청한 스쿼드 신청들 조회에 성공한다.")
        void success() {
        }
    }

    @Nested
    @DisplayName("내가 신청한 스쿼드 신청 취소를 문서화한다.")
    class MyCancel {

        @Test
        @DisplayName("내가 신청한 스쿼드 신청 취소에 성공한다.")
        void success() throws Exception {
            Long CREW_ID = 1L;
            Long SQUAD_ID = 2L;
            doNothing().when(squadParticipantCommandService).cancelMyRequest(any(), eq(CREW_ID), eq(SQUAD_ID));

            mockMvc.perform(delete("/api/crews/{crewId}/squads/{squadId}/requests/me", CREW_ID, SQUAD_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("squads-participants/my-cancel/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("Crew 아이디"),
                                    parameterWithName("squadId").description("Squad 아이디")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("스쿼드 참가 신청을 문서화한다.")
    class Request {

        @Test
        @DisplayName("스쿼드 참가 신청에 성공한다.")
        void success() throws Exception {
            Long CREW_ID = 1L;
            Long SQUAD_ID = 2L;
            doNothing().when(squadParticipantCommandService).request(any(), eq(CREW_ID), eq(SQUAD_ID));

            mockMvc.perform(post("/api/crews/{crewId}/squads/{squadId}/requests", CREW_ID, SQUAD_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(201))
                    .andDo(document("squads-participants/request/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("Crew 아이디"),
                                    parameterWithName("squadId").description("Squad 아이디")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("스쿼드 참가 신청 수락을 문서화한다.")
    class Accept {

        @Test
        @DisplayName("스쿼드 참가 신청 수락에 성공한다.")
        void success() throws Exception {
            Long CREW_ID = 1L;
            Long SQUAD_ID = 2L;
            SquadAcceptRequest ACCEPT_REQUEST = new SquadAcceptRequest(10L);
            doNothing().when(squadParticipantCommandService)
                    .acceptRequest(any(), eq(CREW_ID), eq(SQUAD_ID), any(SquadAcceptDto.class));

            mockMvc.perform(patch("/api/crews/{crewId}/squads/{squadId}/requests", CREW_ID, SQUAD_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .content(objectMapper.writeValueAsString(ACCEPT_REQUEST))
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("squads-participants/accept/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("Crew 아이디"),
                                    parameterWithName("squadId").description("Squad 아이디")
                            ),
                            requestFields(
                                    fieldWithPath("memberId").description("수락하고자 하는 Member 아이디")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("스쿼드 참가 신청 거절을 문서화한다.")
    class Reject {

        @Test
        @DisplayName("스쿼드 참가 신청 거절에 성공한다.")
        void success() throws Exception {
            Long CREW_ID = 1L;
            Long SQUAD_ID = 2L;
            Long REQUEST_ID = 10L;
            doNothing().when(squadParticipantCommandService)
                    .rejectRequest(any(), eq(CREW_ID), eq(SQUAD_ID), eq(REQUEST_ID));

            mockMvc.perform(delete("/api/crews/{crewId}/squads/{squadId}/requests/{requestId}",
                            CREW_ID, SQUAD_ID, REQUEST_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("squads-participants/reject/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("Crew 아이디"),
                                    parameterWithName("squadId").description("Squad 아이디"),
                                    parameterWithName("requestId").description("Request 아이디")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("스쿼드 참가신청들 조회를 문서화한다.")
    class FetchAll {

        @Test
        @DisplayName("스쿼드 참가신청들 조회에 성공한다.")
        void success() throws Exception {
            Long CREW_ID = 1L;
            Long SQUAD_ID = 2L;
            List<SimpleSquadParticipantDto> SERVICE_DTOS = List.of(new SimpleSquadParticipantDto(
                    SQUAD_ID,
                    LocalDateTime.now(),
                    new SimpleMemberInfoDto(
                            1L,
                            null,
                            REVI_NICKNAME_VALUE,
                            REVI_MBTI_VALUE
                    )
            ));
            when(squadParticipantQueryService
                    .fetchAllRequests(any(), eq(CREW_ID), eq(SQUAD_ID), any(Pageable.class)))
                    .thenReturn(SERVICE_DTOS);

            mockMvc.perform(get("/api/crews/{crewId}/squads/{squadId}/requests", CREW_ID, SQUAD_ID)
                            .param("page", "0")
                            .param("size", "1")
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("squads-participants/fetches/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("Crew 아이디"),
                                    parameterWithName("squadId").description("Squad 아이디")
                            ),
                            queryParameters(
                                    parameterWithName("page").description("페이지"),
                                    parameterWithName("size").description("페이지당 사이즈")
                            ),
                            responseBody()
                    ));
        }
    }
}
