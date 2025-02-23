package revi1337.onsquad.inrastructure.s3.error.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.common.error.ErrorCode;
import revi1337.onsquad.inrastructure.s3.error.exception.S3BusinessException;

@RestControllerAdvice
public class S3ExceptionHandler {

    @ExceptionHandler(S3BusinessException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleS3BusinessException(
            S3BusinessException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode, exception.getErrorMessage());
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(errorCode, problemDetail);
        return ResponseEntity.ok().body(restResponse);
    }
}
