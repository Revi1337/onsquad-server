package revi1337.onsquad.common.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import revi1337.onsquad.common.dto.CommonErrorCode;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler({
            ConstraintViolationException.class,
            BindException.class
    })
    public ResponseEntity<RestResponse<ProblemDetail>> handleValidationException(
            Exception exception
    ) {
        CommonErrorCode commonErrorCode = CommonErrorCode.INVALID_INPUT_VALUE;
        ProblemDetail problemDetail = new ValidationExceptionTranslator()
                .translate(commonErrorCode, exception);
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(problemDetail);
        return ResponseEntity.status(commonErrorCode.getStatus()).body(restResponse);
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            HttpRequestMethodNotSupportedException.class
    })
    public ResponseEntity<RestResponse<ProblemDetail>> handleServletException(
            Exception exception
    ) {
        CommonErrorCode commonErrorCode;
        if (exception instanceof MethodArgumentTypeMismatchException) {
            commonErrorCode = CommonErrorCode.PARAMETER_TYPE_MISMATCH;
        } else if (exception instanceof MissingServletRequestParameterException) {
            commonErrorCode = CommonErrorCode.MISSING_PARAMETER;
        } else {
            commonErrorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
        }
        return ResponseEntity.status(commonErrorCode.getStatus())
                .body(RestResponse.fail(ProblemDetail.of(commonErrorCode)));
    }
}
