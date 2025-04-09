package revi1337.onsquad.inrastructure.mail.support;

public record VerificationSnapshot(
        String key,
        VerificationState state
) {
    public boolean canUseBy(long epochMillis) {
        return state.canUseBy(epochMillis);
    }
}
