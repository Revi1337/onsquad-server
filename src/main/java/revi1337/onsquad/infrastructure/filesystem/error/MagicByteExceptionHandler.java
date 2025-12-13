package revi1337.onsquad.infrastructure.filesystem.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.common.error.ErrorCode;

@Deprecated
@RestControllerAdvice
public class MagicByteExceptionHandler {

    @ExceptionHandler(MagicByteValidationException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleAttachmentValidationException(
            MagicByteValidationException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode, exception.getErrorMessage());
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(errorCode, problemDetail);
        return ResponseEntity.ok().body(restResponse);
    }
}
