package revi1337.onsquad.member.error.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.common.error.ErrorCode;
import revi1337.onsquad.member.error.exception.MemberBusinessException;
import revi1337.onsquad.member.error.exception.MemberDomainException;
import revi1337.onsquad.member.error.exception.MemberException;

@RestControllerAdvice
public class MemberExceptionHandler {

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleMemberException(
            MemberException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode);
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(errorCode, problemDetail);
        return ResponseEntity.ok().body(restResponse);
    }

    @ExceptionHandler(MemberDomainException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleMemberDomainException(
            MemberDomainException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode, exception.getErrorMessage());
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(errorCode, problemDetail);
        return ResponseEntity.ok().body(restResponse);
    }

    @ExceptionHandler(MemberBusinessException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleMemberBusinessException(
            MemberBusinessException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode, exception.getErrorMessage());
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(errorCode, problemDetail);
        return ResponseEntity.ok().body(restResponse);
    }
}
