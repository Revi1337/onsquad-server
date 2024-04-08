package revi1337.onsquad.member.dto.response;

public record DuplicateNicknameResponse(
        boolean duplicate
) {
    public static DuplicateNicknameResponse of(boolean duplicate) {
        return new DuplicateNicknameResponse(duplicate);
    }
}
