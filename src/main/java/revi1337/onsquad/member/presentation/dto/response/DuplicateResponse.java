package revi1337.onsquad.member.presentation.dto.response;

public record DuplicateResponse(
        boolean duplicate
) {
    public static DuplicateResponse of(boolean duplicate) {
        return new DuplicateResponse(duplicate);
    }
}
