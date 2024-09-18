package revi1337.onsquad.crew_comment.error.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.common.error.ErrorCode;
import revi1337.onsquad.crew_comment.error.exception.CrewCommentBusinessException;
import revi1337.onsquad.crew_comment.error.exception.CrewCommentDomainException;

@RestControllerAdvice
public class CrewCommentExceptionHandler {

    @ExceptionHandler(CrewCommentDomainException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleCrewCommentDomainException(
            CrewCommentDomainException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode, exception.getErrorMessage());
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(errorCode, problemDetail);
        return ResponseEntity.ok().body(restResponse);
    }

    @ExceptionHandler(CrewCommentBusinessException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleCrewCommentBusinessException(
            CrewCommentBusinessException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode, exception.getErrorMessage());
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(errorCode, problemDetail);
        return ResponseEntity.ok().body(restResponse);
    }
}
