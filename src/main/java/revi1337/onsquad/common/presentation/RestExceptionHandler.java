package revi1337.onsquad.common.presentation;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.common.error.CommonBusinessException;
import revi1337.onsquad.common.error.CommonErrorCode;
import revi1337.onsquad.common.error.ErrorCode;
import revi1337.onsquad.common.error.ValidationExceptionTranslator;

@Slf4j
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception exception,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request
    ) {
        if (isResponseCommitted(request)) {
            return null;
        }
        logException(exception, statusCode);
        CommonErrorCode errorCode = resolveErrorCode(exception, statusCode);
        ProblemDetail problemDetail = createProblemDetail(exception, errorCode);
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(statusCode.value(), problemDetail);
        return ResponseEntity.status(statusCode).headers(headers).body(restResponse);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<Object> handleMultipartException(
            MultipartException exception,
            WebRequest request
    ) {
        HttpStatus status = isSizeLimitExceeded(exception) ? HttpStatus.PAYLOAD_TOO_LARGE : HttpStatus.BAD_REQUEST;
        return handleExceptionInternal(exception, null, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleConstraintViolationException(
            ConstraintViolationException exception
    ) {
        CommonErrorCode commonErrorCode = CommonErrorCode.INVALID_INPUT_VALUE;
        ProblemDetail problemDetail = new ValidationExceptionTranslator()
                .translate(commonErrorCode, exception);
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(commonErrorCode, problemDetail);
        return ResponseEntity.status(restResponse.status()).body(restResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ignored,
            HttpServletRequest httpServletRequest
    ) {
        CommonErrorCode commonErrorCode = CommonErrorCode.ALREADY_REQUEST;
        ProblemDetail problemDetail = ProblemDetail.withFormat(commonErrorCode, httpServletRequest.getRequestURI());
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(commonErrorCode, problemDetail);
        return ResponseEntity.status(restResponse.status()).body(restResponse);
    }

    @ExceptionHandler(CommonBusinessException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleCommonBusinessException(
            CommonBusinessException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode, exception.getErrorMessage());
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(errorCode, problemDetail);
        return ResponseEntity.status(restResponse.status()).body(restResponse);
    }

    private boolean isSizeLimitExceeded(Throwable throwable) {
        for (Throwable cause = throwable; cause != null; cause = cause.getCause()) {
            String message = cause.getMessage();
            if (message == null) {
                continue;
            }
            String lowerCaseMessage = message.toLowerCase(Locale.ROOT);
            if (lowerCaseMessage.contains("exceed") && (lowerCaseMessage.contains("size") || lowerCaseMessage.contains("length"))) {
                return true;
            }
        }
        return false;
    }

    private boolean isResponseCommitted(WebRequest request) {
        return request instanceof ServletWebRequest servletWebRequest
                && servletWebRequest.getResponse() != null
                && servletWebRequest.getResponse().isCommitted();
    }

    private void logException(Exception exception, HttpStatusCode statusCode) {
        if (statusCode.is5xxServerError()) {
            log.error("[{}] status={}", exception.getClass().getSimpleName(), statusCode.value(), exception);
            return;
        }
        log.debug("[{}] status={} message={}", exception.getClass().getSimpleName(), statusCode.value(), exception.getMessage());
    }

    private CommonErrorCode resolveErrorCode(Exception exception, HttpStatusCode statusCode) {
        if (exception instanceof TypeMismatchException) {
            return CommonErrorCode.PARAMETER_TYPE_MISMATCH;
        }
        if (exception instanceof MissingServletRequestParameterException) {
            return CommonErrorCode.MISSING_PARAMETER;
        }
        return CommonErrorCode.fromStatus(statusCode.value());
    }

    private ProblemDetail createProblemDetail(Exception exception, CommonErrorCode errorCode) {
        if (exception instanceof BindException) {
            return new ValidationExceptionTranslator().translate(errorCode, exception);
        }
        return ProblemDetail.of(errorCode);
    }
}
