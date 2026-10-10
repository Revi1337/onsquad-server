package revi1337.onsquad.announce.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import revi1337.onsquad.announce.domain.error.AnnounceBusinessException;
import revi1337.onsquad.announce.domain.error.AnnounceDomainException;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.common.error.ErrorCode;

@RestControllerAdvice
public class AnnounceExceptionHandler {

    @ExceptionHandler(AnnounceBusinessException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleAnnounceBusinessException(
            AnnounceBusinessException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode, exception.getErrorMessage());
        return RestResponse.fail(errorCode, problemDetail).toResponseEntity();
    }

    @ExceptionHandler(AnnounceDomainException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleAnnounceDomainException(
            AnnounceDomainException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode, exception.getErrorMessage());
        return RestResponse.fail(errorCode, problemDetail).toResponseEntity();
    }
}
