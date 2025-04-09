package revi1337.onsquad.inrastructure.mail.application;

import java.util.Arrays;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VerificationStatus {

    SUCCESS("200"),
    FAIL("400");

    private final String code;

    public static Stream<VerificationStatus> stream() {
        return Arrays.stream(VerificationStatus.values());
    }

    public static boolean supports(String code) {
        return stream().anyMatch(status -> status.code.equals(code));
    }
}
