package revi1337.onsquad.member.domain.model;

public record VerificationSnapshot(
        String key,
        VerificationState state
) {

    public boolean canUse(long epochMillis) {
        return state.canUse(epochMillis);
    }

    public String getTarget() {
        return state.email();
    }

    public long getExpireTime() {
        return state.expireTime();
    }

    public String getCode() {
        return state.code();
    }

    public boolean authenticated() {
        return VerificationStatus.stream()
                .anyMatch(status -> status.name().equals(state.code()));
    }
}
