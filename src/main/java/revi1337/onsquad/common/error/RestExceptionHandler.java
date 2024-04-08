package revi1337.onsquad.common.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import revi1337.onsquad.common.dto.ErrorCode;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({
            ConstraintViolationException.class, BindException.class
    })
    public ResponseEntity<RestResponse<ProblemDetail>> handleValidationException(
            Exception exception
    ) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ProblemDetail problemDetail = new ValidationExceptionTranslator()
                .translate(errorCode, exception);
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(problemDetail);
        return ResponseEntity.status(errorCode.getStatus()).body(restResponse);
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class
    })
    public ResponseEntity<RestResponse<ProblemDetail>> handleServletException(
            Exception exception
    ) {
        ErrorCode errorCode;
        if (exception instanceof MethodArgumentTypeMismatchException) {
            errorCode = ErrorCode.PARAMETER_TYPE_MISMATCH;
        } else if (exception instanceof MissingServletRequestParameterException) {
            errorCode = ErrorCode.MISSING_PARAMETER;
        } else {
            errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(errorCode.getStatus())
                .body(RestResponse.fail(ProblemDetail.of(errorCode)));
    }
}
