package revi1337.onsquad.common.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import revi1337.onsquad.common.error.CommonErrorCode;

class RestResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("본문이 없는 성공은 HTTP 200 과 바디 status 200 이고 data 는 빈 문자열이다")
    void ok() throws Exception {
        ResponseEntity<RestResponse<Void>> response = RestResponse.ok().toResponseEntity();

        JsonNode json = objectMapper.valueToTree(response.getBody());
        assertSoftly(softly -> {
            softly.assertThat(response.getStatusCode().value()).isEqualTo(200);
            softly.assertThat(json.get("status").asInt()).isEqualTo(200);
            softly.assertThat(json.get("success").asBoolean()).isTrue();
            softly.assertThat(json.get("data").isTextual()).isTrue();
            softly.assertThat(json.get("data").asText()).isEmpty();
            softly.assertThat(json.has("error")).isFalse();
        });
    }

    @Test
    @DisplayName("본문이 없는 생성은 HTTP 201 과 바디 status 201 이고 data 는 빈 문자열이다")
    void created() {
        ResponseEntity<RestResponse<Void>> response = RestResponse.created().toResponseEntity();

        JsonNode json = objectMapper.valueToTree(response.getBody());
        assertSoftly(softly -> {
            softly.assertThat(response.getStatusCode().value()).isEqualTo(201);
            softly.assertThat(json.get("status").asInt()).isEqualTo(201);
            softly.assertThat(json.get("data").asText()).isEmpty();
        });
    }

    @Test
    @DisplayName("데이터가 있는 생성은 HTTP 201 과 함께 data 를 내려준다")
    void createdWithData() {
        ResponseEntity<RestResponse<String>> response = RestResponse.created("token").toResponseEntity();

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody().data()).isEqualTo("token");
    }

    @Test
    @DisplayName("데이터가 있는 성공은 HTTP 200 과 함께 data 를 내려준다")
    void success() {
        ResponseEntity<RestResponse<List<String>>> response = RestResponse.success(List.of("a", "b")).toResponseEntity();

        assertSoftly(softly -> {
            softly.assertThat(response.getStatusCode().value()).isEqualTo(200);
            softly.assertThat(response.getBody().success()).isTrue();
            softly.assertThat(response.getBody().data()).containsExactly("a", "b");
        });
    }

    @Test
    @DisplayName("에러 코드로 만든 실패는 에러 코드의 상태가 HTTP 상태와 바디 status 가 된다")
    void failWithErrorCode() {
        ProblemDetail problemDetail = ProblemDetail.of(CommonErrorCode.NOT_FOUND);

        ResponseEntity<RestResponse<ProblemDetail>> response = RestResponse.fail(CommonErrorCode.NOT_FOUND, problemDetail).toResponseEntity();

        JsonNode json = objectMapper.valueToTree(response.getBody());
        assertSoftly(softly -> {
            softly.assertThat(response.getStatusCode().value()).isEqualTo(404);
            softly.assertThat(json.get("status").asInt()).isEqualTo(404);
            softly.assertThat(json.get("success").asBoolean()).isFalse();
            softly.assertThat(json.has("data")).isFalse();
            softly.assertThat(json.at("/error/code").asText()).isEqualTo("C005");
        });
    }

    @Test
    @DisplayName("상태 값으로 만든 실패는 그 상태가 HTTP 상태가 된다")
    void failWithStatus() {
        ResponseEntity<RestResponse<ProblemDetail>> response = RestResponse
                .fail(415, ProblemDetail.of(CommonErrorCode.UNSUPPORTED_MEDIA_TYPE)).toResponseEntity();

        assertThat(response.getStatusCode().value()).isEqualTo(415);
        assertThat(response.getBody().status()).isEqualTo(415);
    }
}
