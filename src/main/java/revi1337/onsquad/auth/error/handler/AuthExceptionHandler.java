package revi1337.onsquad.auth.error.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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

    private static final String UNEXPECTED_AUTHENTICATE_EXCEPTION = "unexpected authenticated exception";

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleAuthenticationException(
            AuthenticationException exception
    ) {
        return switch (exception) {
            case UsernameNotFoundException ignored -> {
                ErrorCode errorCode = AuthErrorCode.USERNAME_NOT_FOUND;
                ProblemDetail problemDetail = ProblemDetail.of(errorCode);
                RestResponse<ProblemDetail> restResponse = RestResponse.fail(problemDetail);
                yield ResponseEntity.status(errorCode.getStatus()).body(restResponse);
            }

            case BadCredentialsException ignored -> {
                ErrorCode errorCode = AuthErrorCode.BAD_CREDENTIAL;
                ProblemDetail problemDetail = ProblemDetail.of(errorCode);
                RestResponse<ProblemDetail> restResponse = RestResponse.fail(problemDetail);
                yield ResponseEntity.status(errorCode.getStatus()).body(restResponse);
            }

            default -> throw new RuntimeException(UNEXPECTED_AUTHENTICATE_EXCEPTION);
        };
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
