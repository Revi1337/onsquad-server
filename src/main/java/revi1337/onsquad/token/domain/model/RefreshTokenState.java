package revi1337.onsquad.token.domain.model;

public record RefreshTokenState(
        RefreshToken value,
        String memberId,
        long expireTime
) {

}
