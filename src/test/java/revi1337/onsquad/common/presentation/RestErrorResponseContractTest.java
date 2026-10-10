package revi1337.onsquad.common.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.hashtag.application.HashtagService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class RestErrorResponseContractTest extends ApplicationLayerTestSupport {

    private static final String JSON = "application/json";
    private static final String BOUNDARY = "contract-test-boundary";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @LocalServerPort
    private int port;

    @MockBean(name = "cachedHashtagService")
    private HashtagService hashtagService;

    @Nested
    @DisplayName("성공 응답")
    class success {

        @Test
        @DisplayName("성공 응답은 HTTP 200 과 바디 status 200 이고 error 키가 없다")
        void returns200_withoutErrorKey() throws Exception {
            Result result = send("GET", "/api/categories", null, null, null);

            assertThat(result.httpStatus()).isEqualTo(200);
            assertThat(result.json().get("status").asInt()).isEqualTo(200);
            assertThat(result.json().get("success").asBoolean()).isTrue();
            assertThat(result.json().has("error")).isFalse();
        }
    }

    @Nested
    @DisplayName("Spring MVC 가 던지는 예외")
    class mvcExceptions {

        @Test
        @DisplayName("존재하지 않는 URL 은 404 와 C005 로 응답한다")
        void returns404_whenUrlDoesNotExist() throws Exception {
            Result result = send("GET", "/api/nope", null, null, null);

            assertError(result, 404, "C005");
        }

        @Test
        @DisplayName("지원하지 않는 메서드는 405 와 C003 으로 응답한다")
        void returns405_whenMethodIsNotSupported() throws Exception {
            Result result = send("DELETE", "/api/categories", null, null, null);

            assertError(result, 405, "C003");
        }

        @Test
        @DisplayName("경로 변수 타입이 맞지 않으면 400 과 C004 로 응답한다")
        void returns400_whenPathVariableTypeMismatches() throws Exception {
            Result result = send("GET", "/api/crews/abc/main", null, null, null);

            assertError(result, 400, "C004");
        }

        @Test
        @DisplayName("필수 쿼리 파라미터가 없으면 400 과 C002 로 응답한다")
        void returns400_whenRequiredParameterIsMissing() throws Exception {
            Result result = send("GET", "/api/members/check-nickname", null, null, null);

            assertError(result, 400, "C002");
        }

        @Test
        @DisplayName("지원하지 않는 OAuth 벤더는 500 이 아니라 400 과 C004 로 응답한다")
        void returns400_whenOAuthVendorIsNotSupported() throws Exception {
            Result result = send("GET", "/api/login/oauth2/naver", null, null, null);

            assertError(result, 400, "C004");
        }

        @Test
        @DisplayName("깨진 JSON 본문은 400 과 C001 로 응답한다")
        void returns400_whenBodyIsNotReadable() throws Exception {
            Result result = send("POST", "/api/members", JSON, null, "{not-json");

            assertError(result, 400, "C001");
            assertThat(result.json().at("/error").has("parameters")).isFalse();
        }

        @Test
        @DisplayName("@Valid 검증에 실패하면 400 과 C001 로 응답하고 실패한 필드명을 알려준다")
        void returns400_withInvalidFields_whenValidationFails() throws Exception {
            String body = "{\"email\":\"a@gmail.com\",\"password\":\"Abcd1234!\",\"passwordConfirm\":\"Abcd1234!\","
                    + "\"address\":\"a\",\"addressDetail\":\"b\"}";

            Result result = send("POST", "/api/members", JSON, null, body);

            assertError(result, 400, "C001");
            assertThat(result.json().at("/error/parameters")).extracting(JsonNode::asText).containsExactly("nickname");
        }

        @Test
        @DisplayName("비밀번호를 빼고 가입하면 500 이 아니라 400 과 C001 로 응답하고 누락된 필드명을 알려준다")
        void returns400_withMissingPassword_whenSignUpOmitsIt() throws Exception {
            String body = "{\"email\":\"a@gmail.com\",\"passwordConfirm\":\"Abcd1234!\","
                    + "\"nickname\":\"nick\",\"address\":\"a\",\"addressDetail\":\"b\"}";

            Result result = send("POST", "/api/members", JSON, null, body);

            assertError(result, 400, "C001");
            assertThat(result.json().at("/error/parameters")).extracting(JsonNode::asText).containsExactly("password");
        }

        @Test
        @DisplayName("지원하지 않는 OAuth 벤더는 500 이 아니라 400 과 C004 로 응답한다")
        void returns400_whenOAuth2VendorIsNotSupported() throws Exception {
            Result result = send("GET", "/api/login/oauth2/naver", null, null, null);

            assertError(result, 400, "C004");
        }

        @Test
        @DisplayName("OAuth 콜백도 지원하지 않는 벤더는 400 과 C004 로 응답한다")
        void returns400_whenOAuth2CallbackVendorIsNotSupported() throws Exception {
            Result result = send("GET", "/api/login/oauth2/code/naver?code=abc", null, null, null);

            assertError(result, 400, "C004");
        }

        @Test
        @DisplayName("멀티파트 요청에 필수 파트가 없으면 400 과 C001 로 응답한다")
        void returns400_whenRequiredPartIsMissing() throws Exception {
            String body = "--" + BOUNDARY + "\r\nContent-Disposition: form-data; name=\"other\"\r\n\r\nx\r\n--" + BOUNDARY + "--\r\n";

            Result result = send("POST", "/api/crews", "multipart/form-data; boundary=" + BOUNDARY, null, body);

            assertError(result, 400, "C001");
        }

        @Test
        @DisplayName("지원하지 않는 Content-Type 은 415 와 C010 으로 응답한다")
        void returns415_whenContentTypeIsNotSupported() throws Exception {
            Result result = send("POST", "/api/members", "text/plain", null, "hello");

            assertError(result, 415, "C010");
        }

        @Test
        @DisplayName("업로드 파일이 허용 크기를 넘으면 413 과 C011 로 응답한다")
        void returns413_whenUploadExceedsLimit() throws Exception {
            String body = "--" + BOUNDARY + "\r\nContent-Disposition: form-data; name=\"file\"; filename=\"a.png\"\r\n"
                    + "Content-Type: image/png\r\n\r\n" + "x".repeat(6 * 1024 * 1024) + "\r\n--" + BOUNDARY + "--\r\n";

            Result result = send("POST", "/api/crews", "multipart/form-data; boundary=" + BOUNDARY, null, body);

            assertError(result, 413, "C011");
        }

        @Test
        @DisplayName("Accept 를 만족시킬 수 없으면 406 으로 응답하고 바디는 비어 있다")
        void returns406_withEmptyBody_whenAcceptIsNotSatisfiable() throws Exception {
            Result result = send("GET", "/api/categories", null, "application/xml", null);

            assertThat(result.httpStatus()).isEqualTo(406);
            assertThat(result.body()).isEmpty();
        }
    }

    @Nested
    @DisplayName("애플리케이션 예외")
    class applicationExceptions {

        @Test
        @DisplayName("처리되지 않은 예외는 500 과 C006 으로 응답한다")
        void returns500_whenExceptionIsNotHandled() throws Exception {
            when(hashtagService.findHashtags()).thenThrow(new IllegalStateException("boom"));

            Result result = send("GET", "/api/hashtags", null, null, null);

            assertError(result, 500, "C006");
        }

        @Test
        @DisplayName("토큰 없이 인증이 필요한 API 를 호출하면 401 과 T004 로 응답한다")
        void returns401_whenTokenIsMissing() throws Exception {
            Result result = send("GET", "/api/members/me", null, null, null);

            assertError(result, 401, "T004");
        }

        @Test
        @DisplayName("존재하지 않는 계정으로 로그인하면 401 과 A001 로 응답한다")
        void returns401_whenLoginFails() throws Exception {
            String body = "{\"email\":\"nobody@gmail.com\",\"password\":\"Abcd1234!\"}";

            Result result = send("POST", "/api/auth/login", JSON, null, body);

            assertError(result, 401, "A001");
        }

        @Test
        @DisplayName("로그인 URL 에 GET 으로 요청하면 405 와 C003 으로 응답한다")
        void returns405_whenLoginUrlIsRequestedWithGet() throws Exception {
            Result result = send("GET", "/api/auth/login", null, null, null);

            assertError(result, 405, "C003");
        }
    }

    private void assertError(Result result, int status, String code) {
        assertThat(result.httpStatus()).isEqualTo(status);
        assertThat(result.json().get("status").asInt()).isEqualTo(status);
        assertThat(result.json().get("success").asBoolean()).isFalse();
        assertThat(result.json().at("/error/code").asText()).isEqualTo(code);
    }

    private Result send(String method, String path, String contentType, String accept, String body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
        if (contentType != null) {
            builder.header("Content-Type", contentType);
        }
        if (accept != null) {
            builder.header("Accept", accept);
        }
        builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));

        HttpResponse<String> response = HttpClient.newHttpClient().send(builder.build(), HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body() == null ? "" : response.body();
        JsonNode json = responseBody.isBlank() ? OBJECT_MAPPER.createObjectNode() : OBJECT_MAPPER.readTree(responseBody);
        return new Result(response.statusCode(), responseBody, json);
    }

    private record Result(int httpStatus, String body, JsonNode json) {

    }
}
