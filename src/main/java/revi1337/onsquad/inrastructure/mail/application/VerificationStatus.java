package revi1337.onsquad.inrastructure.mail.application;

import java.util.Arrays;
import java.util.stream.Stream;

public enum VerificationStatus {

    SUCCESS, FAIL;

    public static Stream<VerificationStatus> stream() {
        return Arrays.stream(VerificationStatus.values());
    }
}
