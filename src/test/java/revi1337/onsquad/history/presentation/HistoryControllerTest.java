package revi1337.onsquad.history.presentation;

import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static revi1337.onsquad.common.fixture.HistoryFixture.createCrewAcceptHistory;
import static revi1337.onsquad.common.fixture.HistoryFixture.createCrewCreateHistory;
import static revi1337.onsquad.common.fixture.HistoryFixture.createCrewRejectHistory;
import static revi1337.onsquad.common.fixture.HistoryFixture.createCrewRequestHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.history.application.HistoryQueryService;
import revi1337.onsquad.history.application.response.HistoryResponse;
import revi1337.onsquad.history.domain.HistoryType;

@WebMvcTest(HistoryController.class)
class HistoryControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private HistoryQueryService historyQueryService;

    @Nested
    @DisplayName("사용자 히스토리 조회를 문서화한다.")
    class fetchHistories {

        @Test
        @DisplayName("사용자 히스토리 조회에 성공한다.")
        void success() throws Exception {
            PageRequest pageRequest = PageRequest.of(0, 4);
            LocalDateTime baseTime = LocalDate.of(2026, 1, 4).atStartOfDay();
            List<HistoryResponse> results = getHistoryResponse(baseTime);
            PageResponse<HistoryResponse> pageResponse = PageResponse.from(new PageImpl<>(results, pageRequest, results.size()));
            when(historyQueryService.fetchHistories(any(), any(), any(), any(), any(Pageable.class))).thenReturn(pageResponse);

            mockMvc.perform(get("/api/members/me/histories")
                            .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                            .param("from", baseTime.toLocalDate().toString())
                            .param("to", baseTime.plusDays(5).toLocalDate().toString())
                            .param("page", String.valueOf(pageRequest.getPageNumber()))
                            .param("size", String.valueOf(pageRequest.getPageSize()))
                            .contentType(APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value(200))
                    .andDo(document("histories/success/me",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                            queryParameters(
                                    parameterWithName("from").description("조회 시작 날짜 (yyyy-MM-dd)"),
                                    parameterWithName("to").description("조회 종료 날짜 (yyyy-MM-dd)"),
                                    parameterWithName("type")
                                            .description("활동 유형 (HistoryType): " + Arrays.stream(HistoryType.values()).map(Enum::name).toList()).optional(),
                                    parameterWithName("page").description("페이지 번호 (0부터 시작(1과 동일))").optional(),
                                    parameterWithName("size").description("한 페이지당 개수").optional()
                            ),
                            responseBody()
                    ));
        }
    }

    private List<HistoryResponse> getHistoryResponse(LocalDateTime baseTime) {
        return List.of(
                HistoryResponse.from(createCrewCreateHistory(4L, 1L, 1L, "crew-name-1", baseTime.plusDays(5))),
                HistoryResponse.from(createCrewRequestHistory(3L, 1L, 1L, "crew-name-1", baseTime.plusDays(3))),
                HistoryResponse.from(createCrewAcceptHistory(2L, 1L, "requester", 1L, "crew-name-1", baseTime.plusDays(2))),
                HistoryResponse.from(createCrewRejectHistory(1L, 1L, "requester", 1L, "crew-name-1", baseTime.plusDays(1)))
        );
    }
}
