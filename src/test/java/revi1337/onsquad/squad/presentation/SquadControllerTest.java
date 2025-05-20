package revi1337.onsquad.squad.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
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
import static revi1337.onsquad.category.domain.vo.CategoryType.GAME;
import static revi1337.onsquad.category.domain.vo.CategoryType.MOVIE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_CONTENT_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_DISCORD_LINK_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_KAKAO_LINK_VALUE;
import static revi1337.onsquad.common.fixture.SquadValueFixture.SQUAD_TITLE_VALUE;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.category.presentation.dto.request.CategoryCondition;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad.application.SquadCommandService;
import revi1337.onsquad.squad.application.SquadQueryService;
import revi1337.onsquad.squad.application.dto.SimpleSquadDto;
import revi1337.onsquad.squad.application.dto.SquadDto;
import revi1337.onsquad.squad.application.dto.SquadWithLeaderStateDto;
import revi1337.onsquad.squad.application.dto.SquadWithParticipantAndLeaderAndViewStateDto;
import revi1337.onsquad.squad.presentation.dto.request.SquadCreateRequest;

@WebMvcTest(SquadController.class)
class SquadControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private SquadCommandService squadCommandService;

    @MockBean
    private SquadQueryService squadQueryService;

    @Nested
    @DisplayName("스쿼드 생성을 문서화한다.")
    class NewSquad {

        @Test
        @DisplayName("스쿼드 생성에 성공한다.")
        void success() throws Exception {
            SquadCreateRequest CREATE_REQUEST = new SquadCreateRequest(
                    SQUAD_TITLE_VALUE,
                    SQUAD_CONTENT_VALUE,
                    20,
                    SQUAD_ADDRESS_VALUE,
                    SQUAD_ADDRESS_DETAIL_VALUE,
                    List.of(MOVIE, GAME),
                    SQUAD_KAKAO_LINK_VALUE,
                    SQUAD_DISCORD_LINK_VALUE
            );
            when(squadCommandService.newSquad(any(), anyLong(), eq(CREATE_REQUEST.toDto()))).thenReturn(1L);

            mockMvc.perform(post("/api/crews/{crewId}/squads", 1L)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .content(objectMapper.writeValueAsString(CREATE_REQUEST))
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(201))
                    .andDo(document("squad/success/new",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            requestFields(
                                    fieldWithPath("title").description("스쿼드 제목"),
                                    fieldWithPath("content").description("스쿼드 내용"),
                                    fieldWithPath("capacity").description("스쿼드 수용 인원"),
                                    fieldWithPath("address").description("스쿼드 장소"),
                                    fieldWithPath("addressDetail").description("스쿼드 상세 장소").optional(),
                                    fieldWithPath("categories").description("스쿼드 카테고리 (전체는 무시됩니다.)"),
                                    fieldWithPath("kakaoLink").description("스쿼드 Kakao 링크").optional(),
                                    fieldWithPath("discordLink").description("Discord 링크").optional()
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("스쿼드 조회를 문서화한다.")
    class FetchSquad {

        @Test
        @DisplayName("스쿼드 조회에 성공한다.")
        void success() throws Exception {
            Long CREW_ID = 1L;
            Long SQUAD_ID = 2L;
            SquadWithParticipantAndLeaderAndViewStateDto SERVICE_DTO = new SquadWithParticipantAndLeaderAndViewStateDto(
                    true,
                    true,
                    true,
                    new SquadDto(
                            SQUAD_ID,
                            SQUAD_TITLE_VALUE,
                            SQUAD_CONTENT_VALUE,
                            20,
                            8,
                            SQUAD_ADDRESS_VALUE,
                            SQUAD_ADDRESS_DETAIL_VALUE,
                            SQUAD_KAKAO_LINK_VALUE,
                            SQUAD_DISCORD_LINK_VALUE,
                            List.of(GAME.getText(), MOVIE.getText()),
                            new SimpleMemberInfoDto(
                                    2L,
                                    null,
                                    ANDONG_NICKNAME_VALUE,
                                    ANDONG_MBTI_VALUE
                            )
                    )
            );
            when(squadQueryService.fetchSquad(any(), anyLong(), anyLong())).thenReturn(SERVICE_DTO);

            mockMvc.perform(get("/api/crews/{crewId}/squads/{squadId}", CREW_ID, SQUAD_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("squad/success/fetch",
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
    @DisplayName("스쿼드들 조회를 문서화한다.")
    class FetchSquads {

        @Test
        @DisplayName("스쿼드들 조회에 성공한다.")
        void success() throws Exception {
            Long CREW_ID = 1L;
            Long SQUAD_ID = 2L;
            List<SquadDto> SERVICE_DTOS = List.of(new SquadDto(
                    SQUAD_ID,
                    SQUAD_TITLE_VALUE,
                    SQUAD_CONTENT_VALUE,
                    20,
                    8,
                    SQUAD_ADDRESS_VALUE,
                    SQUAD_ADDRESS_DETAIL_VALUE,
                    SQUAD_KAKAO_LINK_VALUE,
                    SQUAD_DISCORD_LINK_VALUE,
                    List.of(GAME.getText(), MOVIE.getText()),
                    new SimpleMemberInfoDto(
                            2L,
                            null,
                            ANDONG_NICKNAME_VALUE,
                            ANDONG_MBTI_VALUE
                    )
            ));
            when(squadQueryService.fetchSquads(any(), anyLong(), any(CategoryCondition.class), any(Pageable.class)))
                    .thenReturn(SERVICE_DTOS);

            mockMvc.perform(get("/api/crews/{crewId}/squads", CREW_ID)
                            .param("category", "전체")
                            .param("page", "0")
                            .param("size", "3")
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("squad/success/fetches",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            queryParameters(
                                    parameterWithName("category").description("카테고리"),
                                    parameterWithName("page").description("페이지"),
                                    parameterWithName("size").description("페이지당 사이즈")
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("스쿼드 관리를 문서화한다.")
    class FetchSquadsWithOwnerState {

        @Test
        @DisplayName("스쿼드 관리를 문서화한다.")
        void success() throws Exception {
            Long CREW_ID = 1L;
            List<SquadWithLeaderStateDto> SERVICE_DTOS = List.of(new SquadWithLeaderStateDto(
                    true,
                    new SimpleSquadDto(
                            1L,
                            SQUAD_TITLE_VALUE,
                            20,
                            8,
                            List.of(GAME.getText(), MOVIE.getText()),
                            new SimpleMemberInfoDto(
                                    2L,
                                    null,
                                    ANDONG_NICKNAME_VALUE,
                                    ANDONG_MBTI_VALUE
                            )
                    )
            ));
            when(squadQueryService.fetchSquadsWithOwnerState(any(), anyLong(), any(Pageable.class)))
                    .thenReturn(SERVICE_DTOS);

            mockMvc.perform(get("/api/crews/{crewId}/manage/squads", CREW_ID)
                            .param("page", "0")
                            .param("size", "3")
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("squad/success/manage",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            queryParameters(
                                    parameterWithName("page").description("페이지"),
                                    parameterWithName("size").description("페이지당 사이즈")
                            ),
                            responseBody()
                    ));
        }
    }
}
