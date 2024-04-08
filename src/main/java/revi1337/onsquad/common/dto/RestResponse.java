package revi1337.onsquad.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record RestResponse<T> (
        boolean success,
        T data,
        ProblemDetail error
) {
    private RestResponse(T data) {
        this(true, data, null);
    }

    private RestResponse(ProblemDetail problemDetail) {
        this(false, null, problemDetail);
    }

    public static <T> RestResponse<T> success(T data) {
        return new RestResponse<>(data);
    }

    public static <T extends ProblemDetail> RestResponse<T> fail(T problemDetail) {
        return new RestResponse<>(problemDetail);
    }
}
