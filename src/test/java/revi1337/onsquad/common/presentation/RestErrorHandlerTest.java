package revi1337.onsquad.common.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import jakarta.servlet.RequestDispatcher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;

class RestErrorHandlerTest {

    private final RestErrorHandler restErrorHandler = new RestErrorHandler();

    @ParameterizedTest(name = "컨테이너 상태 {0} 은 HTTP {0}, 에러 코드 {1} 로 응답한다")
    @CsvSource({
            "400, C001",
            "404, C005",
            "405, C003",
            "406, C012",
            "413, C011",
            "415, C010",
            "422, C001",
            "500, C006",
            "503, C006"
    })
    @DisplayName("컨테이너가 알려준 오류 상태를 그대로 사용하고 에러 코드만 상태로 결정한다")
    void keepsContainerStatus(int containerStatus, String expectedCode) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, containerStatus);

        ResponseEntity<RestResponse<ProblemDetail>> response = restErrorHandler.handleError(request);

        assertSoftly(softly -> {
            softly.assertThat(response.getStatusCode().value()).isEqualTo(containerStatus);
            softly.assertThat(response.getBody().status()).isEqualTo(containerStatus);
            softly.assertThat(response.getBody().success()).isFalse();
            softly.assertThat(response.getBody().error().code()).isEqualTo(expectedCode);
        });
    }

    @Test
    @DisplayName("오류 상태 정보가 없으면 500 과 C006 으로 응답한다")
    void returns500_whenStatusIsMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        ResponseEntity<RestResponse<ProblemDetail>> response = restErrorHandler.handleError(request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().error().code()).isEqualTo("C006");
    }

    @Test
    @DisplayName("표준에 없는 상태 코드는 500 과 C006 으로 응답한다")
    void returns500_whenStatusIsNotStandard() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 499);

        ResponseEntity<RestResponse<ProblemDetail>> response = restErrorHandler.handleError(request);

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        assertThat(response.getBody().error().code()).isEqualTo("C006");
    }
}
