package revi1337.onsquad.inrastructure.mail.support;

public record VerificationState(
        String code,
        String target,
        long predictedExpiredTime
) {
    public boolean canUseBy(long epochMillis) {
        return predictedExpiredTime > epochMillis;
    }
}
