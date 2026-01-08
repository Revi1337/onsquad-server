package revi1337.onsquad.auth.verification.domain;

import java.util.Arrays;
import java.util.stream.Stream;

public enum VerificationStatus {

    SUCCESS, FAIL;

    public static Stream<VerificationStatus> stream() {
        return Arrays.stream(VerificationStatus.values());
    }
}
