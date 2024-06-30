package revi1337.onsquad.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import revi1337.onsquad.common.error.ErrorCode;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestResponse<T> (
        int status,
        boolean success,
        T data,
        ProblemDetail error
) {
    private RestResponse(int status, T data) {
        this(status, true, data, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> RestResponse<T> ok() {
        return new RestResponse<>(200, (T) "");
    }

    @SuppressWarnings("unchecked")
    public static <T> RestResponse<T> created() {
        return new RestResponse<>(201, (T) "");
    }

    public static <T> RestResponse<T> created(T data) {
        return new RestResponse<>(201, data);
    }

    @SuppressWarnings("unchecked")
    public static <T> RestResponse<T> noContent() {
        return new RestResponse<>(204, (T) "");
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
}
