package revi1337.onsquad.auth.error.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import revi1337.onsquad.auth.error.AuthErrorCode;
import revi1337.onsquad.auth.error.AuthException;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.common.error.ErrorCode;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleAuthenticationException(
            AuthenticationException exception
    ) {
        ErrorCode errorCode = AuthErrorCode.BAD_CREDENTIAL;
        if (exception instanceof UsernameNotFoundException) {
            errorCode = AuthErrorCode.USERNAME_NOT_FOUND;
        }
        ProblemDetail problemDetail = ProblemDetail.of(errorCode);
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(problemDetail);
        return ResponseEntity.status(errorCode.getStatus()).body(restResponse);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleAuthException(
            AuthException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode);
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(problemDetail);
        return ResponseEntity.status(errorCode.getStatus()).body(restResponse);
    }
}
