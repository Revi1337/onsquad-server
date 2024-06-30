package revi1337.onsquad.crew.error.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.common.error.ErrorCode;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew.error.exception.CrewDomainException;

@RestControllerAdvice
public class CrewExceptionHandler {

    @ExceptionHandler(CrewBusinessException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleCrewBusinessException(
            CrewBusinessException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode, exception.getErrorMessage());
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(errorCode, problemDetail);
        return ResponseEntity.ok().body(restResponse);
    }

    @ExceptionHandler(CrewDomainException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleCrewDomainException(
            CrewDomainException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode, exception.getErrorMessage());
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(errorCode, problemDetail);
        return ResponseEntity.ok().body(restResponse);
    }
}
