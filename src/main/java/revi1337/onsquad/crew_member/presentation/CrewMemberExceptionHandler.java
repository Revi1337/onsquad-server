package revi1337.onsquad.crew_member.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.common.error.ErrorCode;
import revi1337.onsquad.crew_member.domain.error.CrewMemberBusinessException;

@RestControllerAdvice
public class CrewMemberExceptionHandler {

    @ExceptionHandler(CrewMemberBusinessException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleCrewMemberBusinessException(
            CrewMemberBusinessException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode, exception.getErrorMessage());
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(errorCode, problemDetail);
        return ResponseEntity.ok().body(restResponse);
    }
}
