package revi1337.onsquad.auth.verification.domain;

import java.util.List;

public record VerificationSnapshots(
        List<VerificationSnapshot> snapshots
) {

    public int size() {
        return snapshots.size();
    }

    public List<VerificationSnapshot> extractAvailableBefore(long epochMillis) {
        return snapshots.stream()
                .filter(snapshot -> snapshot.canUse(epochMillis))
                .toList();
    }
}
