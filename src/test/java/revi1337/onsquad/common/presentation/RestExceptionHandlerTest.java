package revi1337.onsquad.common.presentation;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import revi1337.onsquad.common.PresentationLayerTestSupport;
import revi1337.onsquad.common.error.CommonBusinessException;
import revi1337.onsquad.common.error.CommonErrorCode;

@WebMvcTest(controllers = RestExceptionHandlerTest.TriggerController.class)
@Import(RestExceptionHandlerTest.TriggerController.class)
class RestExceptionHandlerTest extends PresentationLayerTestSupport {

    @Nested
    @DisplayName("Spring MVC 표준 예외")
    class mvcExceptions {

        @Test
        @DisplayName("허용되지 않은 메서드는 405 와 C003 으로 응답하고 Allow 헤더를 유지한다")
        void returns405_withAllowHeader() throws Exception {
            mockMvc.perform(post("/trigger/get-only"))
                    .andExpect(status().isMethodNotAllowed())
                    .andExpect(header().string("Allow", "GET"))
                    .andExpect(jsonPath("$.status").value(405))
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.error.code").value("C003"));
        }

        @Test
        @DisplayName("지원하지 않는 Content-Type 은 415 와 C010 으로 응답한다")
        void returns415() throws Exception {
            mockMvc.perform(post("/trigger/json").contentType("text/plain").content("hello"))
                    .andExpect(status().isUnsupportedMediaType())
                    .andExpect(jsonPath("$.status").value(415))
                    .andExpect(jsonPath("$.error.code").value("C010"))
                    .andExpect(jsonPath("$.error.message").value(CommonErrorCode.UNSUPPORTED_MEDIA_TYPE.getDescription()));
        }

        @Test
        @DisplayName("Accept 를 만족시킬 수 없으면 406 과 C012 로 응답한다")
        void returns406() throws Exception {
            mockMvc.perform(get("/trigger/xml-only").accept(APPLICATION_JSON))
                    .andExpect(status().isNotAcceptable())
                    .andExpect(jsonPath("$.status").value(406))
                    .andExpect(jsonPath("$.error.code").value("C012"));
        }

        @Test
        @DisplayName("경로 변수 타입이 맞지 않으면 400 과 C004 로 응답한다")
        void returns400_whenTypeMismatches() throws Exception {
            mockMvc.perform(get("/trigger/number/abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error.code").value("C004"));
        }

        @Test
        @DisplayName("필수 파라미터가 없으면 400 과 C002 로 응답한다")
        void returns400_whenParameterIsMissing() throws Exception {
            mockMvc.perform(get("/trigger/param"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error.code").value("C002"));
        }

        @Test
        @DisplayName("본문을 읽을 수 없으면 400 과 C001 로 응답한다")
        void returns400_whenBodyIsNotReadable() throws Exception {
            mockMvc.perform(post("/trigger/json").contentType(APPLICATION_JSON).content("{not-json"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error.code").value("C001"))
                    .andExpect(jsonPath("$.error.parameters").doesNotExist());
        }

        @Test
        @DisplayName("@Valid 검증에 실패하면 400 과 C001 로 응답하고 실패한 필드명을 알려준다")
        void returns400_withInvalidFields() throws Exception {
            mockMvc.perform(post("/trigger/json").contentType(APPLICATION_JSON).content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.code").value("C001"))
                    .andExpect(jsonPath("$.error.parameters[0]").value("name"));
        }

        @Test
        @DisplayName("업로드 크기를 초과하면 413 과 C011 로 응답한다")
        void returns413() throws Exception {
            mockMvc.perform(get("/trigger/too-large"))
                    .andExpect(status().isPayloadTooLarge())
                    .andExpect(jsonPath("$.status").value(413))
                    .andExpect(jsonPath("$.error.code").value("C011"));
        }

        @Test
        @DisplayName("원인이 크기 초과인 MultipartException 은 413 과 C011 로 응답한다")
        void returns413_whenMultipartExceptionIsCausedBySizeLimit() throws Exception {
            mockMvc.perform(get("/trigger/multipart-too-large"))
                    .andExpect(status().isPayloadTooLarge())
                    .andExpect(jsonPath("$.status").value(413))
                    .andExpect(jsonPath("$.error.code").value("C011"));
        }

        @Test
        @DisplayName("크기 초과가 아닌 MultipartException 은 400 과 C001 로 응답한다")
        void returns400_whenMultipartExceptionIsNotCausedBySizeLimit() throws Exception {
            mockMvc.perform(get("/trigger/multipart-broken"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error.code").value("C001"));
        }

        @Test
        @DisplayName("서버 측 표준 예외는 해당 상태 그대로 C006 으로 응답한다")
        void returnsServerErrorStatus() throws Exception {
            mockMvc.perform(get("/trigger/async-timeout"))
                    .andExpect(status().isServiceUnavailable())
                    .andExpect(jsonPath("$.status").value(503))
                    .andExpect(jsonPath("$.error.code").value("C006"));
        }
    }

    @Nested
    @DisplayName("공통 애플리케이션 예외")
    class commonExceptions {

        @Test
        @DisplayName("ConstraintViolationException 은 400 과 C001 로 응답한다")
        void returns400_whenConstraintIsViolated() throws Exception {
            mockMvc.perform(get("/trigger/constraint"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.error.code").value("C001"));
        }

        @Test
        @DisplayName("DataIntegrityViolationException 은 409 와 C008 로 응답하고 요청 URI 를 메시지에 담는다")
        void returns409_withRequestUri() throws Exception {
            mockMvc.perform(get("/trigger/integrity"))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.error.code").value("C008"))
                    .andExpect(jsonPath("$.error.message").value("이미 처리된 요청입니다. uri : /trigger/integrity"));
        }

        @Test
        @DisplayName("CommonBusinessException 은 예외가 가진 에러 코드의 상태로 응답한다")
        void returnsErrorCodeStatus_whenCommonBusinessExceptionIsThrown() throws Exception {
            mockMvc.perform(get("/trigger/business"))
                    .andExpect(status().isTooManyRequests())
                    .andExpect(jsonPath("$.status").value(429))
                    .andExpect(jsonPath("$.error.code").value("C007"));
        }
    }

    @RestController
    static class TriggerController {

        record Body(@NotEmpty String name) {

        }

        @GetMapping("/trigger/get-only")
        String getOnly() {
            return "ok";
        }

        @PostMapping(value = "/trigger/json", consumes = "application/json")
        String json(@Valid @RequestBody Body body) {
            return body.name();
        }

        @GetMapping(value = "/trigger/xml-only", produces = "application/xml")
        String xmlOnly() {
            return "<a/>";
        }

        @GetMapping("/trigger/number/{id}")
        String number(@PathVariable Long id) {
            return String.valueOf(id);
        }

        @GetMapping("/trigger/param")
        String param(@RequestParam String name) {
            return name;
        }

        @GetMapping("/trigger/too-large")
        String tooLarge() {
            throw new MaxUploadSizeExceededException(5 * 1024 * 1024);
        }

        @GetMapping("/trigger/multipart-too-large")
        String multipartTooLarge() {
            throw new MultipartException("Could not access multipart servlet request",
                    new IllegalStateException("The field file exceeds its maximum permitted size of 5242880 bytes."));
        }

        @GetMapping("/trigger/multipart-broken")
        String multipartBroken() {
            throw new MultipartException("Failed to parse multipart servlet request");
        }

        @GetMapping("/trigger/async-timeout")
        String asyncTimeout() {
            throw new AsyncRequestTimeoutException();
        }

        @GetMapping("/trigger/constraint")
        String constraint() {
            throw new ConstraintViolationException(Set.of());
        }

        @GetMapping("/trigger/integrity")
        String integrity() {
            throw new DataIntegrityViolationException("duplicate");
        }

        @GetMapping("/trigger/business")
        String business() {
            throw new CommonBusinessException.ToManyRequest(CommonErrorCode.TO_MANY_REQUEST);
        }
    }
}
