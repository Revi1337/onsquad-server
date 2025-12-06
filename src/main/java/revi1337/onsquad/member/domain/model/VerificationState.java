package revi1337.onsquad.member.domain.model;

public record VerificationState(
        String code,
        String email,
        long expireTime
) {

    public boolean canUse(long epochMillis) {
        return expireTime > epochMillis;
    }
}
