package revi1337.onsquad.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import revi1337.onsquad.common.error.ErrorCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestResponse<T>(
        int status,
        boolean success,
        T data,
        ProblemDetail error
) {
    private RestResponse(int status, T data) {
        this(status, true, data, null);
    }

    public static RestResponse<Void> ok() {
        return emptyData(200);
    }

    public static RestResponse<Void> created() {
        return emptyData(201);
    }

    public static <T> RestResponse<T> created(T data) {
        return new RestResponse<>(201, data);
    }

    public static <T> RestResponse<T> success(T data) {
        return new RestResponse<>(200, data);
    }

    public static <T> RestResponse<T> success(HttpStatus httpStatus, T data) {
        return new RestResponse<>(httpStatus.value(), data);
    }

    public static <T extends ProblemDetail> RestResponse<T> fail(ErrorCode errorCode, T problemDetail) {
        return new RestResponse<>(errorCode.getStatus(), false, null, problemDetail);
    }

    public static <T extends ProblemDetail> RestResponse<T> fail(int status, T problemDetail) {
        return new RestResponse<>(status, false, null, problemDetail);
    }

    public ResponseEntity<RestResponse<T>> toResponseEntity() {
        return ResponseEntity.status(status).body(this);
    }

    @SuppressWarnings("unchecked")
    private static <T> RestResponse<T> emptyData(int status) {
        return new RestResponse<>(status, (T) "");
    }
}
