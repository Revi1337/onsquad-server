package revi1337.onsquad.crew.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseBody;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_KAKAO_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_NAME_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW1_IMAGE_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW1_NAME_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW2_IMAGE_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW2_NAME_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_IMAGE_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_KAKAO_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_NAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ANDONG_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;
import static revi1337.onsquad.common.fixture.RequestFixture.JSON_MULTIPART;
import static revi1337.onsquad.common.fixture.RequestFixture.PNG_MULTIPART;
import static revi1337.onsquad.hashtag.domain.entity.vo.HashtagType.ACTIVE;
import static revi1337.onsquad.hashtag.domain.entity.vo.HashtagType.FOODIE;
import static revi1337.onsquad.member.domain.entity.vo.Mbti.ISTP;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.crew.application.CrewCreationCoordinator;
import revi1337.onsquad.crew.application.CrewQueryService;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;
import revi1337.onsquad.crew.application.dto.response.CrewResponse;
import revi1337.onsquad.crew.application.dto.response.CrewWithParticipantStateResponse;
import revi1337.onsquad.crew.presentation.request.CrewCreateRequest;
import revi1337.onsquad.crew.presentation.request.CrewUpdateRequest;
import revi1337.onsquad.crew_member.application.response.JoinedCrewResponse;
import revi1337.onsquad.hashtag.domain.entity.vo.HashtagType;
import revi1337.onsquad.member.application.dto.SimpleMemberDto;

@WebMvcTest(CrewController.class)
class CrewControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private CrewCreationCoordinator crewCreationCoordinator;

    @MockBean
    private CrewQueryService crewQueryService;

    @Nested
    @DisplayName("Crew 이름 중복 체크를 문서화한다.")
    class CheckCrewNameDuplicate {

        @Test
        @DisplayName("Crew 이름 중복 체크에 성공한다.")
        void success() throws Exception {
            when(crewQueryService.checkNameDuplicate(anyString())).thenReturn(true);

            mockMvc.perform(get("/api/crews/check")
                            .param("name", "크루 이름 1")
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("crew/success/name-duplicate",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            queryParameters(parameterWithName("name").description("중복 체크할 Crew 이름")),
                            responseBody()
                    ));
        }

        @Test
        @DisplayName("토큰이 없으면 Crew 이름 중복 체크에 실패한다.")
        void fail() throws Exception {
            mockMvc.perform(get("/api/crews/check")
                            .param("name", "크루 이름 1")
                            .contentType(APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.status").value(401))
                    .andDo(document("crew/fail/name-duplicate",

                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(parameterWithName("name").description("중복 체크할 Crew 이름")),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("Crew 생성을 문서화한다.")
    class NewCrew {

        @Test
        @DisplayName("Crew 생성에 성공한다.")
        void success() throws Exception {
            doNothing().when(crewCreationCoordinator).newCrew(any(), any(CrewCreateDto.class), any(MultipartFile.class));
            CrewCreateRequest CREATE_REQUEST = new CrewCreateRequest(
                    CREW_NAME_VALUE,
                    CREW_INTRODUCE_VALUE,
                    CREW_DETAIL_VALUE,
                    List.of(ACTIVE, FOODIE),
                    CREW_KAKAO_LINK_VALUE
            );
            MockMultipartFile JSON_PART = JSON_MULTIPART("request", objectMapper.writeValueAsString(CREATE_REQUEST));
            MockMultipartFile FILE_PART = PNG_MULTIPART("file", "dummy.png");

            mockMvc.perform(multipart("/api/crews")
                            .file(JSON_PART)
                            .file(FILE_PART)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(MULTIPART_FORM_DATA_VALUE))
                    .andExpect(jsonPath("$.status").value(201))
                    .andDo(document("crew/success/new",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            requestParts(
                                    partWithName("file")
                                            .description("Crew 이미지 파일 (Content-Type: multipart/form-data)"),
                                    partWithName("request")
                                            .description("Crew 생성 요청 JSON 데이터 (Content-Type: application/json)")
                            ),
                            requestPartFields(
                                    "request",
                                    fieldWithPath("name").description("Crew 이름"),
                                    fieldWithPath("introduce").description("Crew 한줄 소개"),
                                    fieldWithPath("detail").description("Crew 상세 정보"),
                                    fieldWithPath("hashtags").description("Crew 해시태그"),
                                    fieldWithPath("kakaoLink").description("Crew 오픈 카카오톡 링크")
                            ),
                            responseBody()
                    ));
        }

        @Test
        @DisplayName("토큰이 없으면 Crew 생성에 실패한다.")
        void fail() throws Exception {
            CrewCreateRequest CREATE_REQUEST = new CrewCreateRequest(
                    CREW_NAME_VALUE,
                    CREW_INTRODUCE_VALUE,
                    CREW_DETAIL_VALUE,
                    List.of(ACTIVE, FOODIE),
                    CREW_KAKAO_LINK_VALUE
            );
            MockMultipartFile JSON_PART = JSON_MULTIPART("request", objectMapper.writeValueAsString(CREATE_REQUEST));
            MockMultipartFile FILE_PART = PNG_MULTIPART("file", "dummy.png");

            mockMvc.perform(multipart("/api/crews")
                            .file(JSON_PART)
                            .file(FILE_PART)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(401))
                    .andDo(document("crew/fail/new",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("Crew 조회를 문서화한다.")
    class FindCrew {

        @Test
        @DisplayName("토큰이 없는 경우에도 Crew 조회에 성공한다.")
        void success() throws Exception {
            CrewResponse CREW_INFO = new CrewResponse(
                    1L,
                    CREW_NAME_VALUE,
                    CREW_INTRODUCE_VALUE,
                    CREW_DETAIL_VALUE,
                    CREW_IMAGE_LINK_VALUE,
                    CREW_KAKAO_LINK_VALUE,
                    List.of(HashtagType.ACTIVE.getText()),
                    1L,
                    new SimpleMemberDto(
                            1L,
                            null,
                            REVI_NICKNAME_VALUE,
                            REVI_INTRODUCE_VALUE,
                            ISTP.name()
                    ),
                    null
            );
            when(crewQueryService.findCrewById(anyLong())).thenReturn(CREW_INFO);

            mockMvc.perform(get("/api/crews/{crewId}", 1L)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.alreadyJoin").doesNotExist())
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.name").value(CREW_NAME_VALUE))
                    .andExpect(jsonPath("$.data.introduce").value(CREW_INTRODUCE_VALUE))
                    .andExpect(jsonPath("$.data.detail").value(CREW_DETAIL_VALUE))
                    .andExpect(jsonPath("$.data.imageUrl").value(CREW_IMAGE_LINK_VALUE))
                    .andExpect(jsonPath("$.data.kakaoLink").value(CREW_KAKAO_LINK_VALUE))
                    .andExpect(jsonPath("$.data.memberCount").value(1))
                    .andExpect(jsonPath("$.data.hashtags[0]").value(HashtagType.ACTIVE.getText()))
                    .andExpect(jsonPath("$.data.owner.id").value(1))
                    .andExpect(jsonPath("$.data.owner.nickname").value(REVI_NICKNAME_VALUE))
                    .andExpect(jsonPath("$.data.owner.mbti").value(ISTP.name()))
                    .andDo(document("crew/success/fetch-non-auth",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            responseBody()
                    ));
        }

        @Test
        @DisplayName("토큰이 있는 경우에도 Crew 조회에 성공한다.")
        void success2() throws Exception {
            CrewWithParticipantStateResponse CREW_INFO = new CrewWithParticipantStateResponse(
                    true,
                    new CrewResponse(
                            1L,
                            CREW_NAME_VALUE,
                            CREW_INTRODUCE_VALUE,
                            CREW_DETAIL_VALUE,
                            CREW_IMAGE_LINK_VALUE,
                            CREW_KAKAO_LINK_VALUE,
                            List.of(HashtagType.ACTIVE.getText()),
                            1L,
                            new SimpleMemberDto(
                                    1L,
                                    null,
                                    REVI_NICKNAME_VALUE,
                                    REVI_INTRODUCE_VALUE,
                                    ISTP.name()
                            ),
                            true
                    )
            );
            when(crewQueryService.findCrewById(anyLong(), anyLong())).thenReturn(CREW_INFO);

            mockMvc.perform(get("/api/crews/{crewId}", 1L)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.alreadyParticipant").value(true))
                    .andExpect(jsonPath("$.data.crew.id").value(1))
                    .andExpect(jsonPath("$.data.crew.name").value(CREW_NAME_VALUE))
                    .andExpect(jsonPath("$.data.crew.introduce").value(CREW_INTRODUCE_VALUE))
                    .andExpect(jsonPath("$.data.crew.detail").value(CREW_DETAIL_VALUE))
                    .andExpect(jsonPath("$.data.crew.imageUrl").value(CREW_IMAGE_LINK_VALUE))
                    .andExpect(jsonPath("$.data.crew.kakaoLink").value(CREW_KAKAO_LINK_VALUE))
                    .andExpect(jsonPath("$.data.crew.memberCount").value(1))
                    .andExpect(jsonPath("$.data.crew.hashtags[0]").value(HashtagType.ACTIVE.getText()))
                    .andExpect(jsonPath("$.data.crew.owner.id").value(1))
                    .andExpect(jsonPath("$.data.crew.owner.nickname").value(REVI_NICKNAME_VALUE))
                    .andExpect(jsonPath("$.data.crew.owner.mbti").value(ISTP.name()))
                    .andDo(document("crew/success/fetch-auth",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(
                                    headerWithName(AUTHORIZATION_HEADER_KEY).optional().description("사용자 JWT 인증 정보")
                            ),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("Crew 업데이트를 문서화한다.")
    class UpdateCrew {

        @Test
        @DisplayName("Crew 업데이트에 성공한다.")
        void success() throws Exception {
            doNothing().when(crewCreationCoordinator).updateCrew(any(), anyLong(), any(CrewUpdateDto.class));
            CrewUpdateRequest UPDATE_REQUEST = new CrewUpdateRequest(
                    CHANGED_CREW_NAME_VALUE,
                    CHANGED_CREW_INTRODUCE_VALUE,
                    CHANGED_CREW_DETAIL_VALUE,
                    List.of(HashtagType.ESCAPE),
                    CHANGED_CREW_KAKAO_LINK_VALUE
            );

            mockMvc.perform(put("/api/crews/{crewId}", 1L)
                            .content(objectMapper.writeValueAsString(UPDATE_REQUEST))
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("crew/success/update",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            requestFields(
                                    fieldWithPath("name").description("변경할 Crew 이름"),
                                    fieldWithPath("introduce").description("변경할 Crew 한줄 소개"),
                                    fieldWithPath("detail").description("변경할 Crew 상세 정보"),
                                    fieldWithPath("hashtags").description("변경할 Crew 해시태그"),
                                    fieldWithPath("kakaoLink").description("변경할 Crew 오픈 카카오 링크")
                            ),
                            responseBody()
                    ));
        }

        @Test
        @DisplayName("토큰이 없으면 Crew 업데이트에 실패한다.")
        void fail() throws Exception {
            CrewUpdateRequest UPDATE_REQUEST = new CrewUpdateRequest(
                    CHANGED_CREW_NAME_VALUE,
                    CHANGED_CREW_INTRODUCE_VALUE,
                    CHANGED_CREW_DETAIL_VALUE,
                    List.of(HashtagType.ESCAPE),
                    CHANGED_CREW_KAKAO_LINK_VALUE
            );

            mockMvc.perform(put("/api/crews/{crewId}", 1L)
                            .content(objectMapper.writeValueAsString(UPDATE_REQUEST))
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(401))
                    .andDo(document("crew/fail/update",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("Crew 삭제를 문서화한다.")
    class DeleteCrew {

        @Test
        @DisplayName("Crew 삭제에 성공한다.")
        void success() throws Exception {
            Long MEMBER_ID = 1L;
            Long CREW_ID = 2L;
            doNothing().when(crewCreationCoordinator).deleteCrew(MEMBER_ID, CREW_ID);

            mockMvc.perform(delete("/api/crews/{crewId}", CREW_ID)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("crew/success/delete",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            responseBody()
                    ));
        }

        @Test
        @DisplayName("토큰이 없으면 Crew 삭제에 실패한다.")
        void fail() throws Exception {
            Long CREW_ID = 2L;
            mockMvc.perform(delete("/api/crews/{crewId}", CREW_ID)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(401))
                    .andDo(document("crew/fail/delete",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            responseBody()
                    ));
        }
    }

    @Nested
    @DisplayName("Crew 이미지 업데이트를 문서화한다.")
    class UpdateCrewImage {

        @Test
        @DisplayName("Crew 이미지 업데이트에 성공한다.")
        void success() throws Exception {
            doNothing().when(crewCreationCoordinator).updateCrewImage(any(), anyLong(), any(MultipartFile.class));
            MockMultipartFile FILE_PART = PNG_MULTIPART("file", "dummy.png");

            mockMvc.perform(multipart("/api/crews/{crewId}/image", 1L)
                            .file(FILE_PART)
                            .with(request -> {
                                request.setMethod(HttpMethod.PATCH.name());
                                return request;
                            })
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(MULTIPART_FORM_DATA_VALUE))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("crew/success/update-image",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            requestParts(
                                    partWithName("file")
                                            .description("변경할 Crew 이미지 파일 (Content-Type: multipart/form-data)")),
                            responseBody()
                    ));
        }

        @Test
        @DisplayName("토큰이 없으면 Crew 이미지 업데이트에 실패한다.")
        void fail() throws Exception {
            MockMultipartFile FILE_PART = PNG_MULTIPART("file", "dummy.png");

            mockMvc.perform(multipart("/api/crews/{crewId}/image", 1L)
                            .file(FILE_PART)
                            .with(request -> {
                                request.setMethod(HttpMethod.PATCH.name());
                                return request;
                            })
                            .contentType(MULTIPART_FORM_DATA_VALUE))
                    .andExpect(jsonPath("$.status").value(401))
                    .andDo(document("crew/fail/update-image",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            requestParts(partWithName("file").description("변경할 Crew 이미지")),
                            responseBody()
                    ));

            verify(crewCreationCoordinator, never()).updateCrewImage(any(), anyLong(), any(MultipartFile.class));
        }
    }

    @Nested
    @DisplayName("Crew 이미지 삭제를 문서화한다.")
    class DeleteCrewImage {

        @Test
        @DisplayName("Crew 이미지 삭제에 성공한다.")
        void success() throws Exception {
            doNothing().when(crewCreationCoordinator).deleteCrewImage(any(), anyLong());

            mockMvc.perform(delete("/api/crews/{crewId}/image", 1L)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("crew/success/delete-image",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            responseBody()
                    ));

            verify(crewCreationCoordinator).deleteCrewImage(any(), anyLong());
        }

        @Test
        @DisplayName("토큰이 없으면 Crew 이미지 삭제에 실패한다.")
        void fail() throws Exception {
            mockMvc.perform(delete("/api/crews/{crewId}/image", 1L)
                            .contentType(MULTIPART_FORM_DATA_VALUE))
                    .andExpect(jsonPath("$.status").value(401))
                    .andDo(document("crew/fail/delete-image",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            responseBody()
                    ));

            verify(crewCreationCoordinator, times(0)).deleteCrewImage(any(), anyLong());
        }
    }

    @Nested
    @DisplayName("Crew 들 조회를 문서화한다.")
    class FetchCrewsByName {

        @Test
        @DisplayName("Crew 들 조회에 성공한다.")
        void success() throws Exception {
            CrewResponse CREW_INFO = new CrewResponse(
                    1L,
                    CREW_NAME_VALUE,
                    CREW_INTRODUCE_VALUE,
                    CREW_DETAIL_VALUE,
                    CREW_IMAGE_LINK_VALUE,
                    CREW_KAKAO_LINK_VALUE,
                    List.of(HashtagType.ACTIVE.getText()),
                    1L,
                    new SimpleMemberDto(
                            1L,
                            null,
                            REVI_NICKNAME_VALUE,
                            REVI_INTRODUCE_VALUE,
                            ISTP.name()
                    ),
                    null
            );
            List<CrewResponse> CREW_INFOS = List.of(CREW_INFO);
            when(crewQueryService.fetchCrewsByName(eq(CREW_NAME_VALUE), any(Pageable.class))).thenReturn(CREW_INFOS);

            mockMvc.perform(get("/api/crews")
                            .param("name", CREW_NAME_VALUE)
                            .param("page", "1")
                            .param("size", "3")
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data[0].id").value(1))
                    .andExpect(jsonPath("$.data[0].name").value(CREW_NAME_VALUE))
                    .andExpect(jsonPath("$.data[0].introduce").value(CREW_INTRODUCE_VALUE))
                    .andExpect(jsonPath("$.data[0].detail").value(CREW_DETAIL_VALUE))
                    .andExpect(jsonPath("$.data[0].imageUrl").value(CREW_IMAGE_LINK_VALUE))
                    .andExpect(jsonPath("$.data[0].kakaoLink").value(CREW_KAKAO_LINK_VALUE))
                    .andExpect(jsonPath("$.data[0].hashtags[0]").value(HashtagType.ACTIVE.getText()))
                    .andExpect(jsonPath("$.data[0].alreadyJoin").doesNotExist())
                    .andExpect(jsonPath("$.data[0].owner.id").value(1))
                    .andExpect(jsonPath("$.data[0].owner.nickname").value(REVI_NICKNAME_VALUE))
                    .andExpect(jsonPath("$.data[0].owner.mbti").value(ISTP.name()))
                    .andDo(document("crew/success/fetches",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(
                                    parameterWithName("name").description("Crew 이름"),
                                    parameterWithName("page").description("페이지").optional(),
                                    parameterWithName("size").description("페이지 당 사이즈").optional()
                            ),
                            responseBody()
                    ));

            verify(crewQueryService, times(1)).fetchCrewsByName(eq(CREW_NAME_VALUE), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("내가 생성한 Crew 조회를 문서화한다.")
    class FetchOwnedCrews {

        @Test
        @DisplayName("내가 생성한 Crew 조회에 성공한다.")
        void success() throws Exception {
            CrewResponse CREW_INFO = new CrewResponse(
                    1L,
                    CREW_NAME_VALUE,
                    CREW_INTRODUCE_VALUE,
                    CREW_DETAIL_VALUE,
                    CREW_IMAGE_LINK_VALUE,
                    CREW_KAKAO_LINK_VALUE,
                    List.of(HashtagType.ACTIVE.getText()),
                    1L,
                    new SimpleMemberDto(
                            1L,
                            null,
                            REVI_NICKNAME_VALUE,
                            REVI_INTRODUCE_VALUE,
                            ISTP.name()
                    ),
                    null
            );
            List<CrewResponse> CREW_INFOS = List.of(CREW_INFO);
            PageRequest PAGE_REQUEST = PageRequest.of(1, 1);
            when(crewQueryService.fetchOwnedCrews(1L, PAGE_REQUEST)).thenReturn(CREW_INFOS);

            mockMvc.perform(get("/api/crews/me/owned")
                            .param("page", String.valueOf(PAGE_REQUEST.getPageNumber() + 1))
                            .param("size", String.valueOf(PAGE_REQUEST.getPageSize()))
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("crew/success/me-owned",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            queryParameters(
                                    parameterWithName("page").description("페이지").optional(),
                                    parameterWithName("size").description("페이지 당 사이즈").optional()
                            ),
                            responseBody()
                    ));

            verify(crewQueryService, times(1)).fetchOwnedCrews(1L, PAGE_REQUEST);
        }
    }

    @Nested
    @DisplayName("내가 참여하고 있는 Crew 에 대한 CrewMember 조회를 문서화한다.")
    class FetchMyParticipants {

        @Test
        @DisplayName("내가 참여하고 있는 Crew 에 대한 CrewMember 조회를 문서화에 성공한다.")
        void success() throws Exception {
            JoinedCrewResponse SERVICE_DTO1 = new JoinedCrewResponse(
                    1L,
                    CREW1_NAME_VALUE,
                    CREW1_IMAGE_LINK_VALUE,
                    true,
                    new SimpleMemberDto(1L, null, REVI_NICKNAME_VALUE, REVI_INTRODUCE_VALUE, REVI_MBTI_VALUE)
            );
            JoinedCrewResponse SERVICE_DTO2 = new JoinedCrewResponse(
                    2L,
                    CREW2_NAME_VALUE,
                    CREW2_IMAGE_LINK_VALUE,
                    false,
                    new SimpleMemberDto(2L, null, ANDONG_NICKNAME_VALUE, REVI_INTRODUCE_VALUE, ANDONG_MBTI_VALUE)
            );
            List<JoinedCrewResponse> SERVICE_DTOS = List.of(SERVICE_DTO1, SERVICE_DTO2);
            when(crewQueryService.fetchParticipantCrews(any())).thenReturn(SERVICE_DTOS);

            mockMvc.perform(get("/api/crews/me/participants")
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("crew/success/me-participants",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            responseBody()
                    ));

            verify(crewQueryService, times(1)).fetchParticipantCrews(any());
        }
    }
}
