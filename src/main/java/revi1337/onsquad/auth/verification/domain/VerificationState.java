package revi1337.onsquad.auth.verification.domain;

public record VerificationState(
        String code,
        String email,
        long expireTime
) {

    public boolean canUse(long epochMillis) {
        return expireTime > epochMillis;
    }
}
