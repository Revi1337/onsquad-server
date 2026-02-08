package revi1337.onsquad.common.dto;

public record DuplicateResponse(
        boolean duplicate
) {

    public static DuplicateResponse of(boolean duplicate) {
        return new DuplicateResponse(duplicate);
    }
}
