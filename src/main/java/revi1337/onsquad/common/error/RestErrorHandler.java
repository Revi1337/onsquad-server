package revi1337.onsquad.common.error;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.common.dto.CommonErrorCode;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;

import static org.springframework.http.HttpStatus.*;

@RestController
public class RestErrorHandler implements ErrorController {

    @RequestMapping("${server.error.path:${error.path:/error}}")
    public ResponseEntity<RestResponse<ProblemDetail>> handleError(HttpServletRequest httpServletRequest) {
        HttpStatus httpStatus = new RequestDispatcherResolver(httpServletRequest).resolveHttpStatus();
        return switch (httpStatus) {
            case BAD_REQUEST -> ResponseEntity.status(BAD_REQUEST)
                    .body(RestResponse.fail(ProblemDetail.of(CommonErrorCode.INVALID_INPUT_VALUE)));

            case NOT_FOUND -> ResponseEntity.status(NOT_FOUND)
                    .body(RestResponse.fail(ProblemDetail.of(CommonErrorCode.NOT_FOUND)));

            default -> ResponseEntity.status(INTERNAL_SERVER_ERROR)
                    .body(RestResponse.fail(ProblemDetail.of(CommonErrorCode.INTERNAL_SERVER_ERROR)));
        };
    }
}
