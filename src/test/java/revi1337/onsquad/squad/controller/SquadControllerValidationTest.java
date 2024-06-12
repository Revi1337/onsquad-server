package revi1337.onsquad.squad.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import revi1337.onsquad.squad.application.SquadService;
import revi1337.onsquad.squad.dto.SquadDto;
import revi1337.onsquad.squad.dto.request.SquadCreateRequest;
import revi1337.onsquad.support.ValidationWithRestDocsTestSupport;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;

@DisplayName("회원가입 api Validation 테스트")
@WebMvcTest(SquadController.class)
class SquadControllerValidationTest extends ValidationWithRestDocsTestSupport {

    @MockBean private SquadService squadService;

    @Nested
    @DisplayName("Squad 모집 글을 작성한다.")
    class CreateNewSquad {

        @DisplayName("입력값 검증에 실패하면 Squad 모집 글을 생성할 수 없다.")
        @MethodSource("parameterizedCreateNewSquad")
        @ParameterizedTest(name = "[{index}]. Argument ({arguments}) {displayName}")
        public void createNewSquad(SquadCreateRequest squadCreateRequest) throws Exception {
            // given & when & then
            mockMvc.perform(
                            post("/api/v1/squad/new")
                                    .content(objectMapper.writeValueAsString(squadCreateRequest))
                                    .contentType(APPLICATION_JSON)
                    )
                    .andExpect(status().isBadRequest());
        }

        static Stream<Arguments> parameterizedCreateNewSquad() {
            return Stream.of(
                    Arguments.of(SquadCreateRequest.of(null, "서울시 강서구", "우장산 롯데캐슬", 10, "제목", "내용", null, null)),
                    Arguments.of(SquadCreateRequest.of(List.of("등산"), null, "우장산 롯데캐슬", 10, "제목", "내용", null, null)),
                    Arguments.of(SquadCreateRequest.of(List.of("등산"), "서울시 강서구", "우장산 롯데캐슬", 10, null, "내용", null, null)),
                    Arguments.of(SquadCreateRequest.of(List.of("등산"), "서울시 강서구", "우장산 롯데캐슬", 10, "제목", null, null, null))
            );
        }

        @DisplayName("[Docs] Squad 모집 글 작성 문서를 작성한다.")
        @Test
        public void CreateNewSquadDocsTest() throws Exception {
            // given
            SquadCreateRequest squadCreateRequest = SquadCreateRequest.of(List.of("등산", "낚시"), "서울시 강서구", "우장산 롯데캐슬", 10, "제목", "내용", "카카오 링크", "디스코드 링크");
            willDoNothing().given(squadService).createNewSquad(any(SquadDto.class));

            // when
            ResultActions resultActions = mockMvc.perform(
                    post("/api/v1/squad/new")
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .content(objectMapper.writeValueAsString(squadCreateRequest))
                            .contentType(APPLICATION_JSON)
            );

            // then
            resultActions
                    .andExpect(status().isCreated())
                    .andDo(
                            document("squad-controller/createNewSquad",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")
                                    ),
                                    requestFields(
                                            fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                            fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                            fieldWithPath("capacity").type(JsonFieldType.NUMBER).description("모집 인원"),
                                            fieldWithPath("address").type(JsonFieldType.STRING).description("주소"),
                                            fieldWithPath("addressDetail").type(JsonFieldType.STRING).description("상세 주소").optional(),
                                            fieldWithPath("categories").type(JsonFieldType.ARRAY).description("카테고리"),
                                            fieldWithPath("kakaoLink").type(JsonFieldType.STRING).description("오픈카톡").optional(),
                                            fieldWithPath("discordLink").type(JsonFieldType.STRING).description("디스코드").optional()
                                    )
                            )
                    );
        }
    }
}