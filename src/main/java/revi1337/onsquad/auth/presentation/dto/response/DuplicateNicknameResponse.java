package revi1337.onsquad.auth.dto.response;

public record DuplicateNicknameResponse(
        boolean duplicate
) {
    public static DuplicateNicknameResponse of(boolean duplicate) {
        return new DuplicateNicknameResponse(duplicate);
    }
}
