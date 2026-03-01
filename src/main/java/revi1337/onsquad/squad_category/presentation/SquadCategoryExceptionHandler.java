package revi1337.onsquad.squad_category.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import revi1337.onsquad.common.dto.ProblemDetail;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.common.error.ErrorCode;
import revi1337.onsquad.squad_category.domain.error.SquadCategoryDomainException;

@RestControllerAdvice
public class SquadCategoryExceptionHandler {

    @ExceptionHandler(SquadCategoryDomainException.class)
    public ResponseEntity<RestResponse<ProblemDetail>> handleSquadCategoryDomainException(
            SquadCategoryDomainException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();
        ProblemDetail problemDetail = ProblemDetail.of(errorCode, exception.getErrorMessage());
        RestResponse<ProblemDetail> restResponse = RestResponse.fail(errorCode, problemDetail);
        return ResponseEntity.ok().body(restResponse);
    }
}
