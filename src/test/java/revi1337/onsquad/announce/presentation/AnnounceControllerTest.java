package revi1337.onsquad.announce.presentation;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
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
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_CONTENT_VALUE;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_CONTENT_VALUE_1;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_VALUE;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_VALUE_1;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;
import static revi1337.onsquad.crew_member.domain.vo.CrewRole.OWNER;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.announce.application.AnnounceCommandService;
import revi1337.onsquad.announce.application.AnnounceQueryService;
import revi1337.onsquad.announce.application.dto.AnnounceCreateDto;
import revi1337.onsquad.announce.application.dto.AnnounceDto;
import revi1337.onsquad.announce.application.dto.AnnounceUpdateDto;
import revi1337.onsquad.announce.application.dto.AnnounceWithFixAndModifyStateDto;
import revi1337.onsquad.announce.application.dto.AnnouncesWithWriteStateDto;
import revi1337.onsquad.announce.presentation.dto.request.AnnounceCreateRequest;
import revi1337.onsquad.announce.presentation.dto.request.AnnounceUpdateRequest;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.crew_member.application.dto.SimpleCrewMemberDto;

@WebMvcTest(AnnounceController.class)
class AnnounceControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private AnnounceCommandService announceCommandService;

    @MockBean
    private AnnounceQueryService announceQueryService;

    @Nested
    @DisplayName("Announce 생성을 문서화한다.")
    class Create {

        @Test
        @DisplayName("Announce 생성에 성공한다.")
        void success() throws Exception {
            Long CREW_ID = 1L;
            Long ANNOUNCE_ID = 1L;
            AnnounceCreateRequest CREATE_DTO = new AnnounceCreateRequest(
                    ANNOUNCE_TITLE_VALUE, ANNOUNCE_CONTENT_VALUE
            );
            when(announceCommandService.newAnnounce(any(), eq(CREW_ID), any(AnnounceCreateDto.class)))
                    .thenReturn(ANNOUNCE_ID);

            mockMvc.perform(post("/api/crews/{crewId}/announces", CREW_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .content(objectMapper.writeValueAsString(CREATE_DTO))
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(201))
                    .andDo(document("crew-announce/success/new",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            requestFields(
                                    fieldWithPath("title").description("Announce 제목"),
                                    fieldWithPath("content").description("Announce 내용")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("Announce 조회를 문서화한다")
    class Find {

        @Test
        @DisplayName("Announce 조회에 성공한다.")
        void success1() throws Exception {
            Long MEMBER_ID = 1L;
            Long CREW_ID = 2L;
            Long ANNOUNCE_ID = 3L;
            LocalDateTime NOW = LocalDateTime.now();
            AnnounceWithFixAndModifyStateDto SERVICE_DTO = new AnnounceWithFixAndModifyStateDto(
                    true,
                    true,
                    new AnnounceDto(
                            ANNOUNCE_ID,
                            ANNOUNCE_TITLE_VALUE,
                            ANNOUNCE_CONTENT_VALUE,
                            NOW.minusHours(1),
                            true,
                            NOW,
                            new SimpleCrewMemberDto(
                                    MEMBER_ID,
                                    REVI_NICKNAME_VALUE,
                                    OWNER.getText()
                            )
                    )
            );
            when(announceQueryService.findAnnounce(any(), eq(CREW_ID), eq(ANNOUNCE_ID)))
                    .thenReturn(SERVICE_DTO);

            mockMvc.perform(get("/api/crews/{crewId}/announces/{announceId}", CREW_ID, ANNOUNCE_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("crew-announce/success/fetch",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("Crew 아이디"),
                                    parameterWithName("announceId").description("Announce 아이디")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("Announces 조회를 문서화한다")
    class Finds {

        @Test
        @DisplayName("Announces 조회에 성공한다.")
        void success() throws Exception {
            Long MEMBER_ID = 1L;
            Long CREW_ID = 2L;
            Long ANNOUNCE_ID = 3L;
            PageRequest PAGE_REQUEST = PageRequest.of(0, 10);
            LocalDateTime NOW = LocalDateTime.now();
            AnnouncesWithWriteStateDto SERVICE_DTO = new AnnouncesWithWriteStateDto(
                    true,
                    List.of(new AnnounceDto(
                            ANNOUNCE_ID,
                            ANNOUNCE_TITLE_VALUE,
                            ANNOUNCE_CONTENT_VALUE,
                            NOW.minusHours(1),
                            true,
                            NOW,
                            new SimpleCrewMemberDto(
                                    MEMBER_ID,
                                    REVI_NICKNAME_VALUE,
                                    OWNER.getText()
                            )
                    ))
            );
            when(announceQueryService.findAnnounces(any(), eq(CREW_ID), eq(PAGE_REQUEST)))
                    .thenReturn(SERVICE_DTO);

            mockMvc.perform(get("/api/crews/{crewId}/announces", CREW_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .queryParam("page", "0")
                            .queryParam("size", "10")
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("crew-announce/success/fetches",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("Crew 아이디")
                            ),
                            queryParameters(
                                    parameterWithName("page").description("페이지").optional(),
                                    parameterWithName("size").description("페이지 당 사이즈").optional()
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("Announce 업데이트를 문서화한다.")
    class Update {

        @Test
        @DisplayName("Announce 업데이트를 성공한다.")
        void success1() throws Exception {
            Long CREW_ID = 1L;
            Long ANNOUNCE_ID = 1L;
            doNothing().when(announceCommandService)
                    .updateAnnounce(any(), eq(CREW_ID), eq(ANNOUNCE_ID), any(AnnounceUpdateDto.class));
            AnnounceUpdateRequest UPDATE_REQUEST =
                    new AnnounceUpdateRequest(ANNOUNCE_TITLE_VALUE_1, ANNOUNCE_CONTENT_VALUE_1);

            mockMvc.perform(put("/api/crews/{crewId}/announces/{announceId}", CREW_ID, ANNOUNCE_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .content(objectMapper.writeValueAsString(UPDATE_REQUEST))
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("crew-announce/success/update",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("Crew 아이디"),
                                    parameterWithName("announceId").description("Announce 아이디")
                            ),
                            requestFields(
                                    fieldWithPath("title").description("변경할 Announce 제목"),
                                    fieldWithPath("content").description("변경할 Announce 내용")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("Announce 상단 고정 및 해제를 문서화한다.")
    class Fix {

        @Test
        @DisplayName("Announce 상단 고정에 성공한다.")
        void success1() throws Exception {
            Long CREW_ID = 1L;
            Long ANNOUNCE_ID = 1L;
            boolean fixState = true;
            doNothing().when(announceCommandService)
                    .fixOrUnfixAnnounce(any(), eq(CREW_ID), eq(ANNOUNCE_ID), eq(fixState));

            mockMvc.perform(patch("/api/crews/{crewId}/announces/{announceId}/fix", CREW_ID, ANNOUNCE_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .queryParam("state", "true")
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("crew-announce/success/fix",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("Crew 아이디"),
                                    parameterWithName("announceId").description("Announce 아이디")
                            ),
                            queryParameters(
                                    parameterWithName("state").description("상단 고정 상태 (true는 고정. false는 해제)")
                            ),
                            responseBody()
                    ));
        }

        @Test
        @DisplayName("Announce 상단 해제에 성공한다.")
        void success2() throws Exception {
            Long CREW_ID = 1L;
            Long ANNOUNCE_ID = 1L;
            boolean fixState = false;
            doNothing().when(announceCommandService)
                    .fixOrUnfixAnnounce(any(), eq(CREW_ID), eq(ANNOUNCE_ID), eq(fixState));

            mockMvc.perform(patch("/api/crews/{crewId}/announces/{announceId}/fix", CREW_ID, ANNOUNCE_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .queryParam("state", "false")
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204));
        }
    }

    @Nested
    @DisplayName("Announce 삭제를 문서화한다.")
    class Delete {

        @Test
        @DisplayName("Announce 삭제에 성공한다.")
        void success1() throws Exception {
            Long CREW_ID = 1L;
            Long ANNOUNCE_ID = 1L;
            doNothing().when(announceCommandService).deleteAnnounce(any(), eq(CREW_ID), eq(ANNOUNCE_ID));

            mockMvc.perform(delete("/api/crews/{crewId}/announces/{announceId}", CREW_ID, ANNOUNCE_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("crew-announce/success/delete",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("Crew 아이디"),
                                    parameterWithName("announceId").description("Announce 아이디")
                            ),
                            responseBody()
                    ));
        }
    }
}
