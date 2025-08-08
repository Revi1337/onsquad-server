package revi1337.onsquad.inrastructure.mail.support;

public record VerificationState(
        String code,
        String email,
        long expireTime
) {
    public boolean canUse(long epochMillis) {
        return expireTime > epochMillis;
    }
}
