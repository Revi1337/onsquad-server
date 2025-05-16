package revi1337.onsquad.crew_participant.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_IMAGE_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_KAKAO_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_NAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_NICKNAME_VALUE;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.crew.application.dto.SimpleCrewInfoDto;
import revi1337.onsquad.crew_participant.application.CrewParticipantCommandService;
import revi1337.onsquad.crew_participant.application.CrewParticipantQueryService;
import revi1337.onsquad.crew_participant.application.dto.CrewRequestDto;
import revi1337.onsquad.crew_participant.application.dto.CrewRequestWithCrewDto;
import revi1337.onsquad.crew_participant.application.dto.CrewRequestWithMemberDto;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;

@WebMvcTest(CrewParticipantController.class)
class CrewParticipantControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private CrewParticipantCommandService crewParticipantCommandService;

    @MockBean
    private CrewParticipantQueryService crewParticipantQueryService;

    @Nested
    @DisplayName("Crew 참가신청을 문서화한다.")
    class RequestInCrew {

        @Test
        @DisplayName("Crew 참가신청에 성공한다.")
        void success() throws Exception {
            Long CREW_Id = 1L;
            doNothing().when(crewParticipantCommandService).request(any(), anyLong());

            mockMvc.perform(post("/api/crews/{crewId}/requests", CREW_Id)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(201))
                    .andDo(document("crew-participants/new/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("크루 Id")),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("Crew 참가신청 수락을 문서화한다.")
    class AcceptCrewRequest {

        @Test
        @DisplayName("Crew 참가신청에 성공한다.")
        void success() throws Exception {
            Long CREW_Id = 1L;
            Long REQUEST_ID = 3L;
            doNothing().when(crewParticipantCommandService).acceptRequest(any(), eq(CREW_Id), eq(REQUEST_ID));

            mockMvc.perform(patch("/api/crews/{crewId}/requests/{requestId}", CREW_Id, REQUEST_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("crew-participants/accept/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("크루 Id"),
                                    parameterWithName("requestId").description("크루 참가 신청 Id")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("Crew 참가신청 거절을 문서화한다.")
    class RejectCrewRequest {

        @Test
        @DisplayName("Crew 참가신청에 성공한다.")
        void success() throws Exception {
            Long CREW_Id = 1L;
            Long REQUEST_ID = 2L;
            doNothing().when(crewParticipantCommandService).rejectRequest(any(), eq(CREW_Id), eq(REQUEST_ID));

            mockMvc.perform(delete("/api/crews/{crewId}/requests/{requestId}", CREW_Id, REQUEST_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("crew-participants/reject/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("크루 Id"),
                                    parameterWithName("requestId").description("크루 참가 신청 Id")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("특정 Crew 의 참가신청 관리 목록을 문서화한다.")
    class FetchCrewRequests {

        @Test
        @DisplayName("특정 Crew 의 참가신청 관리 목록을 성공한다.")
        void success() throws Exception {
            Long CREW_Id = 1L;
            List<CrewRequestWithMemberDto> SERVICE_DTOS = List.of(new CrewRequestWithMemberDto(
                    new CrewRequestDto(2L, LocalDateTime.now()),
                    new SimpleMemberInfoDto(1L, null, ANDONG_NICKNAME_VALUE, ANDONG_MBTI_VALUE)
            ));
            when(crewParticipantQueryService.fetchAllRequests(any(), anyLong(), any(Pageable.class)))
                    .thenReturn(SERVICE_DTOS);

            mockMvc.perform(get("/api/crews/{crewId}/manage/requests", CREW_Id)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .param("page", "0")
                            .param("size", "3")
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("crew-participants/fetch/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("크루 Id")),
                            queryParameters(
                                    parameterWithName("page").description("페이지"),
                                    parameterWithName("size").description("페이지당 사이즈")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("내가 신청한 Crew 취소를 문서화한다.")
    class CancelCrewRequest {

        @Test
        @DisplayName("내가 신청한 Crew 취소에 성공한다.")
        void success() throws Exception {
            Long CREW_Id = 1L;
            doNothing().when(crewParticipantCommandService).cancelMyRequest(any(), anyLong());

            mockMvc.perform(delete("/api/crews/{crewId}/requests/me", CREW_Id)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("crew-participants/my-cancel/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("크루 Id")),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("내가 보낸 Crew 신청들 조회를 문서화한다.")
    class FetchAllCrewRequests {

        @Test
        @DisplayName("내가 보낸 Crew 신청들 조회에 성공한다.")
        void success() throws Exception {
            List<CrewRequestWithCrewDto> SERVICE_DTOS = List.of(new CrewRequestWithCrewDto(
                    new CrewRequestDto(3L, LocalDateTime.now()),
                    new SimpleCrewInfoDto(
                            1L,
                            CREW_NAME_VALUE,
                            CREW_INTRODUCE_VALUE,
                            CREW_KAKAO_LINK_VALUE,
                            CREW_IMAGE_LINK_VALUE,
                            new SimpleMemberInfoDto(2L, null, ANDONG_NICKNAME_VALUE, ANDONG_MBTI_VALUE)
                    )
            ));
            when(crewParticipantQueryService.fetchAllCrewRequests(any())).thenReturn(SERVICE_DTOS);

            mockMvc.perform(get("/api/crew-requests/me")
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("crew-participants/me/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            responseBody()
                    ));
        }
    }
}

//package revi1337.onsquad.crew_participant.presentation;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.http.MediaType.APPLICATION_JSON;
//import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
//import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
//import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
//import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
//import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
//import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
//import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
//import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
//import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
//import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
//import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
//import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
//import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
//import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_IMAGE_LINK_VALUE;
//import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_INTRODUCE_VALUE;
//import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_KAKAO_LINK_VALUE;
//import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_NAME_VALUE;
//import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_MBTI_VALUE;
//import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_NICKNAME_VALUE;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.Pageable;
//import revi1337.onsquad.common.PresentationLayerTestSupport;
//import revi1337.onsquad.crew.application.dto.SimpleCrewInfoDto;
//import revi1337.onsquad.crew_participant.application.CrewParticipantService;
//import revi1337.onsquad.crew_participant.application.dto.CrewRequestDto;
//import revi1337.onsquad.crew_participant.application.dto.CrewRequestWithCrewDto;
//import revi1337.onsquad.crew_participant.application.dto.CrewRequestWithMemberDto;
//import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
//
//@WebMvcTest(CrewParticipantController.class)
//class CrewParticipantControllerTest extends PresentationLayerTestSupport {
//
//    @MockBean
//    private CrewParticipantService crewParticipantService;
//
//    @Nested
//    @DisplayName("Crew 참가신청을 문서화한다.")
//    class RequestInCrew {
//
//        @Test
//        @DisplayName("Crew 참가신청에 성공한다.")
//        void success() throws Exception {
//            Long CREW_Id = 1L;
//            doNothing().when(crewParticipantService).requestInCrew(any(), anyLong());
//
//            mockMvc.perform(post("/api/crews/{crewId}/requests", CREW_Id)
//                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
//                            .contentType(APPLICATION_JSON))
//                    .andExpect(jsonPath("$.status").value(201))
//                    .andDo(document("crew-participants/new/success",
//                            preprocessRequest(prettyPrint()),
//                            preprocessResponse(prettyPrint()),
//                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
//                            pathParameters(parameterWithName("crewId").description("크루 Id")),
//                            responseBody()
//                    ));
//        }
//    }
//
//    @Nested
//    @DisplayName("Crew 참가신청 수락을 문서화한다.")
//    class AcceptCrewRequest {
//
//        @Test
//        @DisplayName("Crew 참가신청에 성공한다.")
//        void success() throws Exception {
//            Long CREW_Id = 1L;
//            Long REQUEST_ID = 3L;
//            doNothing().when(crewParticipantService).acceptCrewRequest(any(), eq(CREW_Id), eq(REQUEST_ID));
//
//            mockMvc.perform(patch("/api/crews/{crewId}/requests/{requestId}", CREW_Id, REQUEST_ID)
//                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
//                            .contentType(APPLICATION_JSON))
//                    .andExpect(jsonPath("$.status").value(204))
//                    .andDo(document("crew-participants/accept/success",
//                            preprocessRequest(prettyPrint()),
//                            preprocessResponse(prettyPrint()),
//                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
//                            pathParameters(
//                                    parameterWithName("crewId").description("크루 Id"),
//                                    parameterWithName("requestId").description("크루 참가 신청 Id")
//                            ),
//                            responseBody()
//                    ));
//        }
//    }
//
//    @Nested
//    @DisplayName("Crew 참가신청 거절을 문서화한다.")
//    class RejectCrewRequest {
//
//        @Test
//        @DisplayName("Crew 참가신청에 성공한다.")
//        void success() throws Exception {
//            Long CREW_Id = 1L;
//            Long REQUEST_ID = 2L;
//            doNothing().when(crewParticipantService).rejectCrewRequest(any(), eq(CREW_Id), eq(REQUEST_ID));
//
//            mockMvc.perform(delete("/api/crews/{crewId}/requests/{requestId}", CREW_Id, REQUEST_ID)
//                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
//                            .contentType(APPLICATION_JSON))
//                    .andExpect(jsonPath("$.status").value(204))
//                    .andDo(document("crew-participants/reject/success",
//                            preprocessRequest(prettyPrint()),
//                            preprocessResponse(prettyPrint()),
//                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
//                            pathParameters(
//                                    parameterWithName("crewId").description("크루 Id"),
//                                    parameterWithName("requestId").description("크루 참가 신청 Id")
//                            ),
//                            responseBody()
//                    ));
//        }
//    }
//
//    @Nested
//    @DisplayName("특정 Crew 의 참가신청 목록 조회를 문서화한다.")
//    class FetchCrewRequests {
//
//        @Test
//        @DisplayName("특정 Crew 의 참가신청 목록 조회에 성공한다.")
//        void success() throws Exception {
//            Long CREW_Id = 1L;
//            List<CrewRequestWithMemberDto> SERVICE_DTOS = List.of(new CrewRequestWithMemberDto(
//                    new CrewRequestDto(2L, LocalDateTime.now()),
//                    new SimpleMemberInfoDto(1L, null, ANDONG_NICKNAME_VALUE, ANDONG_MBTI_VALUE)
//            ));
//            when(crewParticipantService.fetchCrewRequests(any(), anyLong(), any(Pageable.class)))
//                    .thenReturn(SERVICE_DTOS);
//
//            mockMvc.perform(get("/api/crews/{crewId}/requests", CREW_Id)
//                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
//                            .param("page", "0")
//                            .param("size", "3")
//                            .contentType(APPLICATION_JSON))
//                    .andExpect(jsonPath("$.status").value(200))
//                    .andDo(document("crew-participants/fetch/success",
//                            preprocessRequest(prettyPrint()),
//                            preprocessResponse(prettyPrint()),
//                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
//                            pathParameters(parameterWithName("crewId").description("크루 Id")),
//                            queryParameters(
//                                    parameterWithName("page").description("페이지"),
//                                    parameterWithName("size").description("페이지당 사이즈")
//                            ),
//                            responseBody()
//                    ));
//        }
//    }
//
//    @Nested
//    @DisplayName("내가 신청한 Crew 취소를 문서화한다.")
//    class CancelCrewRequest {
//
//        @Test
//        @DisplayName("내가 신청한 Crew 취소에 성공한다.")
//        void success() throws Exception {
//            Long CREW_Id = 1L;
//            doNothing().when(crewParticipantService).cancelCrewRequest(any(), anyLong());
//
//            mockMvc.perform(delete("/api/crews/{crewId}/requests/me", CREW_Id)
//                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
//                            .contentType(APPLICATION_JSON))
//                    .andExpect(jsonPath("$.status").value(204))
//                    .andDo(document("crew-participants/my-cancel/success",
//                            preprocessRequest(prettyPrint()),
//                            preprocessResponse(prettyPrint()),
//                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
//                            pathParameters(parameterWithName("crewId").description("크루 Id")),
//                            responseBody()
//                    ));
//        }
//    }
//
//    @Nested
//    @DisplayName("내가 보낸 Crew 신청들 조회를 문서화한다.")
//    class FetchAllCrewRequests {
//
//        @Test
//        @DisplayName("내가 보낸 Crew 신청들 조회에 성공한다.")
//        void success() throws Exception {
//            List<CrewRequestWithCrewDto> SERVICE_DTOS = List.of(new CrewRequestWithCrewDto(
//                    new CrewRequestDto(3L, LocalDateTime.now()),
//                    new SimpleCrewInfoDto(
//                            1L,
//                            CREW_NAME_VALUE,
//                            CREW_INTRODUCE_VALUE,
//                            CREW_KAKAO_LINK_VALUE,
//                            CREW_IMAGE_LINK_VALUE,
//                            new SimpleMemberInfoDto(2L, null, ANDONG_NICKNAME_VALUE, ANDONG_MBTI_VALUE)
//                    )
//            ));
//            when(crewParticipantService.fetchAllCrewRequests(any())).thenReturn(SERVICE_DTOS);
//
//            mockMvc.perform(get("/api/crew-requests/me")
//                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
//                            .contentType(APPLICATION_JSON))
//                    .andExpect(jsonPath("$.status").value(200))
//                    .andDo(document("crew-participants/me/success",
//                            preprocessRequest(prettyPrint()),
//                            preprocessResponse(prettyPrint()),
//                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
//                            responseBody()
//                    ));
//        }
//    }
//}
