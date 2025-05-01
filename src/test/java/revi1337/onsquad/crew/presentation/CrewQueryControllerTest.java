package revi1337.onsquad.crew.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
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
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_MBTI_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;
import static revi1337.onsquad.member.domain.vo.Mbti.ISTP;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.crew.application.CrewQueryService;
import revi1337.onsquad.crew.application.dto.CrewInfoDto;
import revi1337.onsquad.crew_member.application.dto.EnrolledCrewDto;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;
import revi1337.onsquad.member.application.dto.SimpleMemberInfoDto;

@WebMvcTest(CrewQueryController.class)
class CrewQueryControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private CrewQueryService crewQueryService;

    @Nested
    @DisplayName("Crew 이름 중복 체크를 문서화한다.")
    class CheckCrewNameDuplicate {

        @Test
        @DisplayName("Crew 이름 중복 체크에 성공한다.")
        void success() throws Exception {
            when(crewQueryService.isDuplicateCrewName(anyString())).thenReturn(true);

            mockMvc.perform(get("/api/crews/check")
                            .param("name", "크루 이름 1")
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("crews/name-duplicate/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            queryParameters(parameterWithName("name").description("중복 체크할 Crew 이름")),
                            responseBody()
                    ));

            verify(crewQueryService).isDuplicateCrewName(anyString());
        }

        @Test
        @DisplayName("토큰이 없으면 Crew 이름 중복 체크에 실패한다.")
        void fail() throws Exception {
            mockMvc.perform(get("/api/crews/check")
                            .param("name", "크루 이름 1")
                            .contentType(APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.status").value(401))
                    .andDo(document("crews/name-duplicate/fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(parameterWithName("name").description("중복 체크할 Crew 이름")),
                            responseBody()
                    ));

            verify(crewQueryService, times(0)).isDuplicateCrewName(anyString());
        }
    }

    @Nested
    @DisplayName("Crew 조회를 문서화한다.")
    class FindCrew {

        @Test
        @DisplayName("토큰이 없는 경우에도 Crew 조회에 성공한다.")
        void success() throws Exception {
            CrewInfoDto CREW_INFO = new CrewInfoDto(
                    1L,
                    CREW_NAME_VALUE,
                    CREW_INTRODUCE_VALUE,
                    CREW_DETAIL_VALUE,
                    CREW_IMAGE_LINK_VALUE,
                    CREW_KAKAO_LINK_VALUE,
                    List.of(HashtagType.ACTIVE.name()),
                    1L,
                    new SimpleMemberInfoDto(
                            1L,
                            null,
                            REVI_NICKNAME_VALUE, ISTP.name()),
                    null
            );
            when(crewQueryService.findCrewById(anyLong())).thenReturn(CREW_INFO);

            mockMvc.perform(get("/api/crews/{crewId}", 1L)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.name").value(CREW_NAME_VALUE))
                    .andExpect(jsonPath("$.data.introduce").value(CREW_INTRODUCE_VALUE))
                    .andExpect(jsonPath("$.data.detail").value(CREW_DETAIL_VALUE))
                    .andExpect(jsonPath("$.data.imageUrl").value(CREW_IMAGE_LINK_VALUE))
                    .andExpect(jsonPath("$.data.kakaoLink").value(CREW_KAKAO_LINK_VALUE))
                    .andExpect(jsonPath("$.data.hashtags[0]").value(1))
                    .andExpect(jsonPath("$.data.hashtags[1]").value(HashtagType.ACTIVE.name()))
                    .andExpect(jsonPath("$.data.alreadyJoin").doesNotExist())
                    .andExpect(jsonPath("$.data.crewOwner.id").value(1))
                    .andExpect(jsonPath("$.data.crewOwner.nickname").value(REVI_NICKNAME_VALUE))
                    .andExpect(jsonPath("$.data.crewOwner.mbti").value(ISTP.name()))
                    .andDo(document("crews/find-crew/success1",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            responseBody()
                    ));

            verify(crewQueryService, times(1)).findCrewById(anyLong());
        }

        @Test
        @DisplayName("토큰이 있는 경우에도 Crew 조회에 성공한다.")
        void success2() throws Exception {
            CrewInfoDto CREW_INFO = new CrewInfoDto(
                    1L,
                    CREW_NAME_VALUE,
                    CREW_INTRODUCE_VALUE,
                    CREW_DETAIL_VALUE,
                    CREW_IMAGE_LINK_VALUE,
                    CREW_KAKAO_LINK_VALUE,
                    List.of(HashtagType.ACTIVE.name()),
                    1L,
                    new SimpleMemberInfoDto(
                            1L,
                            null,
                            REVI_NICKNAME_VALUE, ISTP.name()),
                    true
            );
            when(crewQueryService.findCrewById(any(), anyLong())).thenReturn(CREW_INFO);

            mockMvc.perform(get("/api/crews/{crewId}", 1L)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.name").value(CREW_NAME_VALUE))
                    .andExpect(jsonPath("$.data.introduce").value(CREW_INTRODUCE_VALUE))
                    .andExpect(jsonPath("$.data.detail").value(CREW_DETAIL_VALUE))
                    .andExpect(jsonPath("$.data.imageUrl").value(CREW_IMAGE_LINK_VALUE))
                    .andExpect(jsonPath("$.data.kakaoLink").value(CREW_KAKAO_LINK_VALUE))
                    .andExpect(jsonPath("$.data.hashtags[0]").value(1))
                    .andExpect(jsonPath("$.data.hashtags[1]").value(HashtagType.ACTIVE.name()))
                    .andExpect(jsonPath("$.data.alreadyJoin").isBoolean())
                    .andExpect(jsonPath("$.data.crewOwner.id").value(1))
                    .andExpect(jsonPath("$.data.crewOwner.nickname").value(REVI_NICKNAME_VALUE))
                    .andExpect(jsonPath("$.data.crewOwner.mbti").value(ISTP.name()))
                    .andDo(document("crews/find-crew/success2",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            responseBody()
                    ));

            verify(crewQueryService, times(1)).findCrewById(any(), anyLong());
        }
    }

    @Nested
    @DisplayName("Crew 들 조회를 문서화한다.")
    class FetchCrewsByName {

        @Test
        @DisplayName("Crew 들 조회에 성공한다.")
        void success() throws Exception {
            CrewInfoDto CREW_INFO = new CrewInfoDto(
                    1L,
                    CREW_NAME_VALUE,
                    CREW_INTRODUCE_VALUE,
                    CREW_DETAIL_VALUE,
                    CREW_IMAGE_LINK_VALUE,
                    CREW_KAKAO_LINK_VALUE,
                    List.of(HashtagType.ACTIVE.name()),
                    1L,
                    new SimpleMemberInfoDto(
                            1L,
                            null,
                            REVI_NICKNAME_VALUE, ISTP.name()),
                    null
            );
            List<CrewInfoDto> CREW_INFOS = List.of(CREW_INFO);
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
                    .andExpect(jsonPath("$.data[0].hashtags[0]").value(1))
                    .andExpect(jsonPath("$.data[0].hashtags[1]").value(HashtagType.ACTIVE.name()))
                    .andExpect(jsonPath("$.data[0].alreadyJoin").doesNotExist())
                    .andExpect(jsonPath("$.data[0].crewOwner.id").value(1))
                    .andExpect(jsonPath("$.data[0].crewOwner.nickname").value(REVI_NICKNAME_VALUE))
                    .andExpect(jsonPath("$.data[0].crewOwner.mbti").value(ISTP.name()))
                    .andDo(document("crews/find-crews/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(
                                    parameterWithName("name").description("Crew 이름"),
                                    parameterWithName("page").description("페이지").optional(),
                                    parameterWithName("size").description("크기").optional()
                            ),
                            responseBody()
                    ));

            verify(crewQueryService, times(1)).fetchCrewsByName(eq(CREW_NAME_VALUE), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("내가 참여하고 있는 Crew 에 대한 CrewMember 들 조회를 테스트한다.")
    class FetchAllJoinedCrews {

        @Test
        @DisplayName("내가 참여하고 있는 Crew 에 대한 CrewMember 들 조회에 성공한다.")
        void success() throws Exception {
            EnrolledCrewDto SERVICE_DTO1 = new EnrolledCrewDto(
                    1L,
                    CREW1_NAME_VALUE,
                    CREW1_IMAGE_LINK_VALUE,
                    true,
                    new SimpleMemberInfoDto(1L, null, REVI_NICKNAME_VALUE, REVI_MBTI_VALUE)
            );
            EnrolledCrewDto SERVICE_DTO2 = new EnrolledCrewDto(
                    2L,
                    CREW2_NAME_VALUE,
                    CREW2_IMAGE_LINK_VALUE,
                    false,
                    new SimpleMemberInfoDto(2L, null, ANDONG_NICKNAME_VALUE, ANDONG_MBTI_VALUE)
            );
            List<EnrolledCrewDto> SERVICE_DTOS = List.of(SERVICE_DTO1, SERVICE_DTO2);
            when(crewQueryService.fetchAllJoinedCrews(any())).thenReturn(SERVICE_DTOS);

            mockMvc.perform(get("/api/crews/me")
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("crew-me/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            responseBody()
                    ));

            verify(crewQueryService).fetchAllJoinedCrews(any());
        }
    }
}
