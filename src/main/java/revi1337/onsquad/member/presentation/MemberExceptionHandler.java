package revi1337.onsquad.member.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.common.error.ErrorCode;
import revi1337.onsquad.member.error.DuplicateNickname;
import revi1337.onsquad.member.error.UnsatisfiedEmailAuthentication;

@RestControllerAdvice
public class MemberExceptionHandler {

    @ExceptionHandler({UnsatisfiedEmailAuthentication.class,})
    public ResponseEntity<RestResponse<ProblemDetail>> handleUnsatisfiedEmailAuthentication(
            UnsatisfiedEmailAuthentication exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode);
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(problemDetail);
        return ResponseEntity.status(errorCode.getStatus()).body(restResponse);
    }

    @ExceptionHandler({DuplicateNickname.class})
    public ResponseEntity<RestResponse<ProblemDetail>> handleDuplicateNickname(
            DuplicateNickname exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode);
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(problemDetail);
        return ResponseEntity.status(errorCode.getStatus()).body(restResponse);
    }
}
