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
import static revi1337.onsquad.common.fixture.HistoryFixture.createCrewAcceptHistory;
import static revi1337.onsquad.common.fixture.HistoryFixture.createCrewCreateHistory;
import static revi1337.onsquad.common.fixture.HistoryFixture.createCrewRejectHistory;
import static revi1337.onsquad.common.fixture.HistoryFixture.createCrewRequestHistory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.history.application.HistoryQueryService;
import revi1337.onsquad.history.application.response.HistoryResponse;

@WebMvcTest(HistoryController.class)
class HistoryControllerTest extends PresentationLayerTestSupport {

    @MockBean
    private HistoryQueryService historyQueryService;

    @Test
    @DisplayName("사용자 히스토리를 문서화한다.")
    void fetchHistories() throws Exception {
        LocalDateTime now = LocalDate.of(2026, 1, 4).atStartOfDay();
        HistoryResponse response1 = HistoryResponse.from(createCrewCreateHistory(4L, 1L, 1L, "crew-name-1", now.plusDays(4)));
        HistoryResponse response2 = HistoryResponse.from(createCrewRequestHistory(3L, 1L, 1L, "crew-name-1", now.plusDays(3)));
        HistoryResponse response3 = HistoryResponse.from(createCrewAcceptHistory(2L, 1L, "requester", 1L, "crew-name-1", now.plusDays(2)));
        HistoryResponse response4 = HistoryResponse.from(createCrewRejectHistory(1L, 1L, "requester", 1L, "crew-name-1", now.plusDays(1)));
        List<HistoryResponse> results = List.of(response1, response2, response3, response4);
        Page<HistoryResponse> page = new PageImpl<>(results, PageRequest.of(0, 4), results.size());
        PageResponse<HistoryResponse> pageResponse = PageResponse.from(page);
        when(historyQueryService.fetchHistories(any(), any(), any(), any(), any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get("/api/members/me/histories")
                        .header(AUTHORIZATION_HEADER_KEY, AUTHORIZATION_HEADER_VALUE)
                        .param("from", "2026-01-01")
                        .param("to", "2026-01-07")
                        .param("page", "1")
                        .param("size", "4")
                        .contentType(APPLICATION_JSON))
                .andDo(document("histories/success/me",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestHeaders(headerWithName(AUTHORIZATION_HEADER_KEY).description("사용자 JWT 인증 정보")),
                        queryParameters(
                                parameterWithName("from").description("조회 시작 날짜 (yyyy-MM-dd)"),
                                parameterWithName("to").description("조회 종료 날짜 (yyyy-MM-dd)"),
                                parameterWithName("type").description("활동 유형 (HistoryType Enum)").optional(),
                                parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                                parameterWithName("size").description("한 페이지당 개수").optional()
                        ),
                        responseBody()
                ));
    }
}
