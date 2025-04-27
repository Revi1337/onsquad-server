package revi1337.onsquad.crew.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
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
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_KAKAO_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_NAME_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_KAKAO_LINK_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_NAME_VALUE;
import static revi1337.onsquad.common.fixture.RequestFixture.JSON_MULTIPART;
import static revi1337.onsquad.common.fixture.RequestFixture.PNG_MULTIPART;
import static revi1337.onsquad.hashtag.domain.vo.HashtagType.ACTIVE;
import static revi1337.onsquad.hashtag.domain.vo.HashtagType.FOODIE;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.crew.application.CrewCommandExecutor;
import revi1337.onsquad.crew.application.dto.CrewCreateDto;
import revi1337.onsquad.crew.application.dto.CrewUpdateDto;
import revi1337.onsquad.crew.presentation.dto.request.CrewCreateRequest;
import revi1337.onsquad.crew.presentation.dto.request.CrewUpdateRequest;
import revi1337.onsquad.hashtag.domain.vo.HashtagType;

@WebMvcTest(CrewCommandController.class)
class CrewCommandControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private CrewCommandExecutor crewCommandExecutor;

    @Nested
    @DisplayName("Crew 생성을 문서화한다.")
    class NewCrew {

        @Test
        @DisplayName("Crew 생성에 성공한다.")
        void success() throws Exception {
            doNothing().when(crewCommandExecutor).newCrew(any(), any(CrewCreateDto.class), any(MultipartFile.class));
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
                    .andDo(document("crews/new/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            requestParts(
                                    partWithName("file").description("Crew 이미지"),
                                    partWithName("request").description("Crew 정보 JSON")
                            ),
                            requestPartFields(
                                    "request",
                                    fieldWithPath("name").description("Crew 이름"),
                                    fieldWithPath("introduce").description("Crew 한줄 소개"),
                                    fieldWithPath("detail").description("Crew 상세 정보"),
                                    fieldWithPath("hashtags").description("Crew 해시태그"),
                                    fieldWithPath("kakaoLink").description("Crew 오픈 카카오 링크")
                            ),
                            responseBody()
                    ));

            verify(crewCommandExecutor).newCrew(any(), any(CrewCreateDto.class), any(MultipartFile.class));
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
                    .andDo(document("crews/new/fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseBody()
                    ));

            verify(crewCommandExecutor, times(0))
                    .newCrew(anyLong(), any(CrewCreateDto.class), any(MultipartFile.class));
        }
    }

    @Nested
    @DisplayName("Crew 업데이트를 문서화한다.")
    class UpdateCrew {

        @Test
        @DisplayName("Crew 업데이트에 성공한다.")
        void success() throws Exception {
            doNothing().when(crewCommandExecutor).updateCrew(any(), anyLong(), any(CrewUpdateDto.class));
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
                    .andDo(document("crews/update/success",
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

            verify(crewCommandExecutor).updateCrew(any(), anyLong(), any(CrewUpdateDto.class));
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
                    .andDo(document("crews/update/fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            responseBody()
                    ));

            verify(crewCommandExecutor, times(0)).updateCrew(any(), anyLong(), any(CrewUpdateDto.class));
        }
    }

    @Nested
    @DisplayName("Crew 삭제를 문서화한다.")
    class DeleteCrew {

        @Test
        @DisplayName("Crew 삭제에 성공한다.")
        void success() throws Exception {
            doNothing().when(crewCommandExecutor).deleteCrew(any(), anyLong());

            mockMvc.perform(delete("/api/crews/{crewId}", 1L)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("crews/delete/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            responseBody()
                    ));

            verify(crewCommandExecutor).deleteCrew(any(), anyLong());
        }

        @Test
        @DisplayName("토큰이 없으면 Crew 삭제에 실패한다.")
        void fail() throws Exception {
            mockMvc.perform(delete("/api/crews/{crewId}", 1L)
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(401))
                    .andDo(document("crews/delete/fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            responseBody()
                    ));

            verify(crewCommandExecutor, times(0)).deleteCrew(any(), anyLong());
        }
    }

    @Nested
    @DisplayName("Crew 이미지 업데이트를 문서화한다.")
    class UpdateCrewImage {

        @Test
        @DisplayName("Crew 이미지 업데이트에 성공한다.")
        void success() throws Exception {
            doNothing().when(crewCommandExecutor).updateCrewImage(any(), anyLong(), any(MultipartFile.class));
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
                    .andDo(document("crews/update-image/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            requestParts(partWithName("file").description("변경할 Crew 이미지")),
                            responseBody()
                    ));

            verify(crewCommandExecutor).updateCrewImage(any(), anyLong(), any(MultipartFile.class));
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
                    .andDo(document("crews/update-image/fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            requestParts(partWithName("file").description("변경할 Crew 이미지")),
                            responseBody()
                    ));

            verify(crewCommandExecutor, times(0)).updateCrewImage(any(), anyLong(), any(MultipartFile.class));
        }
    }

    @Nested
    @DisplayName("Crew 이미지 삭제를 문서화한다.")
    class DeleteCrewImage {

        @Test
        @DisplayName("Crew 이미지 삭제에 성공한다.")
        void success() throws Exception {
            doNothing().when(crewCommandExecutor).deleteCrewImage(any(), anyLong());

            mockMvc.perform(delete("/api/crews/{crewId}/image", 1L)
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .contentType(MULTIPART_FORM_DATA_VALUE))
                    .andExpect(jsonPath("$.status").value(204))
                    .andDo(document("crews/delete-image/success",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            responseBody()
                    ));

            verify(crewCommandExecutor).deleteCrewImage(any(), anyLong());
        }

        @Test
        @DisplayName("토큰이 없으면 Crew 이미지 삭제에 실패한다.")
        void fail() throws Exception {
            mockMvc.perform(delete("/api/crews/{crewId}/image", 1L)
                            .contentType(MULTIPART_FORM_DATA_VALUE))
                    .andExpect(jsonPath("$.status").value(401))
                    .andDo(document("crews/delete-image/fail",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            pathParameters(parameterWithName("crewId").description("Crew 아이디")),
                            responseBody()
                    ));

            verify(crewCommandExecutor, times(0)).deleteCrewImage(any(), anyLong());
        }
    }
}