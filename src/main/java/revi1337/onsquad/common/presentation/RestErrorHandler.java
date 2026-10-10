package revi1337.onsquad.common.presentation;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.common.error.CommonErrorCode;
import revi1337.onsquad.common.error.RequestDispatcherResolver;

@RestController
public class RestErrorHandler implements ErrorController {

    @RequestMapping("${server.error.path:${error.path:/error}}")
    public ResponseEntity<RestResponse<ProblemDetail>> handleError(HttpServletRequest httpServletRequest) {
        int status = new RequestDispatcherResolver(httpServletRequest).resolveHttpStatus().value();
        CommonErrorCode errorCode = CommonErrorCode.fromStatus(status);
        return RestResponse.fail(status, ProblemDetail.of(errorCode)).toResponseEntity();
    }
}
