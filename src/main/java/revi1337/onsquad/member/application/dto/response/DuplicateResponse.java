package revi1337.onsquad.member.application.dto.response;

public record DuplicateResponse(
        boolean duplicate
) {

    public static DuplicateResponse of(boolean duplicate) {
        return new DuplicateResponse(duplicate);
    }
}
