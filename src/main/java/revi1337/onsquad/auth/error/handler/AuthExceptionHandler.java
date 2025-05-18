package revi1337.onsquad.auth.error.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import revi1337.onsquad.auth.error.AuthErrorCode;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.common.error.ErrorCode;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleAuthenticationException(
            AuthenticationException ignored
    ) {
        ErrorCode errorCode = AuthErrorCode.INVALID_CREDENTIALS;
        ProblemDetail problemDetail = ProblemDetail.of(errorCode);
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(errorCode, problemDetail);
        return ResponseEntity.ok().body(restResponse);
    }

    @Deprecated(forRemoval = true)
    public ResponseEntity<RestResponse<ProblemDetail>> deprecatedHandleAuthenticationException(
            AuthenticationException exception
    ) {
        return switch (exception) {
            case UsernameNotFoundException ignored -> {
                ErrorCode errorCode = AuthErrorCode.USERNAME_NOT_FOUND;
                ProblemDetail problemDetail = ProblemDetail.of(errorCode);
                RestResponse<ProblemDetail> restResponse = RestResponse.fail(errorCode, problemDetail);
                yield ResponseEntity.ok().body(restResponse);
            }

            case BadCredentialsException ignored -> {
                ErrorCode errorCode = AuthErrorCode.BAD_CREDENTIAL;
                ProblemDetail problemDetail = ProblemDetail.of(errorCode);
                RestResponse<ProblemDetail> restResponse = RestResponse.fail(errorCode, problemDetail);
                yield ResponseEntity.ok().body(restResponse);
            }

            default -> throw new RuntimeException("unexpected authenticated exception");
        };
    }
}
