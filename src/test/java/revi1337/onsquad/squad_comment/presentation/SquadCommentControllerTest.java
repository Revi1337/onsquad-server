package revi1337.onsquad.squad_comment.presentation;

import static org.mockito.ArgumentMatchers.any;
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
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.KWANGWON_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;
import revi1337.onsquad.squad_comment.application.SquadCommentCommandService;
import revi1337.onsquad.squad_comment.application.SquadCommentQueryService;
import revi1337.onsquad.squad_comment.application.dto.SquadCommentDto;
import revi1337.onsquad.squad_comment.presentation.dto.request.CommentCreateRequest;

@WebMvcTest(SquadCommentController.class)
class SquadCommentControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private SquadCommentCommandService squadCommentCommandService;

    @MockBean
    private SquadCommentQueryService squadCommentQueryService;

    @Nested
    @DisplayName("댓글 생성을 문서화한다.")
    class Add {

        @Test
        @DisplayName("댓글 생성에 성공한다.")
        void success() throws Exception {
            Long MEMBER_ID = 1L;
            Long CREW_ID = 1L;
            Long SQUAD_ID = 2L;
            CommentCreateRequest REQUEST = new CommentCreateRequest("content");
            when(squadCommentCommandService.add(MEMBER_ID, CREW_ID, SQUAD_ID, REQUEST.content())).thenReturn(1L);

            mockMvc.perform(post("/api/crews/{crewId}/squads/{squadId}/comments", CREW_ID, SQUAD_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .content(objectMapper.writeValueAsString(REQUEST))
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(201))
                    .andDo(document("squad-comment/success/new",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("Crew 아이디"),
                                    parameterWithName("squadId").description("Squad 아이디")
                            ),
                            requestFields(fieldWithPath("content").description("댓글 내용")),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("대댓글 생성을 문서화한다.")
    class AddReply {

        @Test
        @DisplayName("댓글 생성에 성공한다.")
        void success() throws Exception {
            Long MEMBER_ID = 1L;
            Long CREW_ID = 1L;
            Long SQUAD_ID = 2L;
            Long PARENT_ID = 3L;
            CommentCreateRequest REQUEST = new CommentCreateRequest("content");
            when(squadCommentCommandService.addReply(MEMBER_ID, CREW_ID, SQUAD_ID, PARENT_ID, REQUEST.content())).thenReturn(1L);

            mockMvc.perform(post("/api/crews/{crewId}/squads/{squadId}/replies/{parentId}", CREW_ID, SQUAD_ID, PARENT_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .content(objectMapper.writeValueAsString(REQUEST))
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(201))
                    .andDo(document("squad-comment/success/reply-new",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("Crew 아이디"),
                                    parameterWithName("squadId").description("Squad 아이디"),
                                    parameterWithName("parentId").description("Comment 아이디")
                            ),
                            requestFields(fieldWithPath("content").description("대댓글 내용")),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("초기 댓글 조회를 문서화한다.")
    class FetchInitialComments {

        @Test
        @DisplayName("초기 댓글 조회에 성공한다.")
        void success() throws Exception {
            Long MEMBER_ID = 1L;
            Long CREW_ID = 1L;
            Long SQUAD_ID = 2L;
            int CHILD_SIZE = 2;
            when(squadCommentQueryService.fetchInitialComments(eq(MEMBER_ID), eq(CREW_ID), eq(SQUAD_ID), any(PageRequest.class), eq(CHILD_SIZE)))
                    .thenReturn(List.of(
                            new SquadCommentDto(null,
                                    1L,
                                    "parent_1",
                                    LocalDateTime.now(),
                                    LocalDateTime.now(),
                                    new SimpleMemberInfoDto(1L, REVI_EMAIL_VALUE, REVI_NICKNAME_VALUE, REVI_MBTI_VALUE),
                                    List.of(
                                            new SquadCommentDto(
                                                    1L,
                                                    2L,
                                                    "child_1",
                                                    LocalDateTime.now(),
                                                    LocalDateTime.now(),
                                                    new SimpleMemberInfoDto(2L, ANDONG_EMAIL_VALUE, ANDONG_NICKNAME_VALUE, ANDONG_MBTI_VALUE),
                                                    List.of()
                                            ),
                                            new SquadCommentDto(
                                                    1L,
                                                    3L,
                                                    "child_2",
                                                    LocalDateTime.now(),
                                                    LocalDateTime.now(),
                                                    new SimpleMemberInfoDto(1L, REVI_EMAIL_VALUE, REVI_NICKNAME_VALUE, REVI_MBTI_VALUE),
                                                    List.of()
                                            )
                                    )
                            ),
                            new SquadCommentDto(
                                    null,
                                    4L,
                                    "parent_2",
                                    LocalDateTime.now(),
                                    LocalDateTime.now(),
                                    new SimpleMemberInfoDto(3L, KWANGWON_EMAIL_VALUE, KWANGWON_NICKNAME_VALUE, KWANGWON_MBTI_VALUE),
                                    List.of(
                                            new SquadCommentDto(
                                                    4L,
                                                    5L,
                                                    "child_3",
                                                    LocalDateTime.now(),
                                                    LocalDateTime.now(),
                                                    new SimpleMemberInfoDto(2L, ANDONG_EMAIL_VALUE, ANDONG_NICKNAME_VALUE, ANDONG_MBTI_VALUE),
                                                    List.of()
                                            ),
                                            new SquadCommentDto(
                                                    4L,
                                                    6L,
                                                    "child_4",
                                                    LocalDateTime.now(),
                                                    LocalDateTime.now(),
                                                    new SimpleMemberInfoDto(1L, REVI_EMAIL_VALUE, REVI_NICKNAME_VALUE, REVI_MBTI_VALUE),
                                                    List.of()
                                            )
                                    )
                            )));

            mockMvc.perform(get("/api/crews/{crewId}/squads/{squadId}/comments", CREW_ID, SQUAD_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .param("page", "1")
                            .param("size", "2")
                            .param("childSize", "2")
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("squad-comment/success/initial-fetch",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("Crew 아이디"),
                                    parameterWithName("squadId").description("Squad 아이디")
                            ),
                            queryParameters(
                                    parameterWithName("page").description("최상위 댓글 페이지 사이즈").optional(),
                                    parameterWithName("size").description("최상위 댓글 페이지 당 사이즈").optional(),
                                    parameterWithName("childSize").description("부모 댓글 당 대댓글 사이즈").optional()
                            ),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("대댓글 더보기를 문서화한다.")
    class FetchMoreChildren {

        @Test
        @DisplayName("대댓글 더보기에 성공한다.")
        void success() throws Exception {
            Long MEMBER_ID = 1L;
            Long CREW_ID = 1L;
            Long SQUAD_ID = 2L;
            Long PARENT_ID = 3L;
            when(squadCommentQueryService.fetchMoreChildren(eq(MEMBER_ID), eq(CREW_ID), eq(SQUAD_ID), eq(PARENT_ID), any(PageRequest.class)))
                    .thenReturn(List.of(
                            new SquadCommentDto(
                                    PARENT_ID,
                                    7L,
                                    "child_4",
                                    LocalDateTime.now(),
                                    LocalDateTime.now(),
                                    new SimpleMemberInfoDto(2L, ANDONG_EMAIL_VALUE, ANDONG_NICKNAME_VALUE, ANDONG_MBTI_VALUE),
                                    List.of()
                            ),
                            new SquadCommentDto(
                                    PARENT_ID,
                                    8L,
                                    "child_5",
                                    LocalDateTime.now(),
                                    LocalDateTime.now(),
                                    new SimpleMemberInfoDto(1L, REVI_EMAIL_VALUE, REVI_NICKNAME_VALUE, REVI_MBTI_VALUE),
                                    List.of()
                            )
                    ));

            mockMvc.perform(get("/api/crews/{crewId}/squads/{squadId}/replies/{parentId}", CREW_ID, SQUAD_ID, PARENT_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .param("page", "1")
                            .param("size", "2")
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("squad-comment/success/replies-fetch",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(
                                    parameterWithName("crewId").description("Crew 아이디"),
                                    parameterWithName("squadId").description("Squad 아이디"),
                                    parameterWithName("parentId").description("Comment 아이디")
                            ),
                            queryParameters(
                                    parameterWithName("page").description("페이지 사이즈").optional(),
                                    parameterWithName("size").description("페이지 당 사이즈").optional()
                            ),
                            responseBody()
                    ));
        }
    }
}
