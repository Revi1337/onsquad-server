package revi1337.onsquad.auth.verification.application;

import java.util.concurrent.ThreadLocalRandom;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VerificationCodeGenerator {

    private static final int VERIFICATION_CODE_LENGTH = 8;

    @Value("${spring.mail.code-seed}")
    private String seed;

    public String generate() {
        StringBuilder stringBuilder = new StringBuilder();
        mapCodeBookToIndex(stringBuilder);
        return stringBuilder.toString();
    }

    private void mapCodeBookToIndex(StringBuilder stringBuilder) {
        ThreadLocalRandom.current().ints(0, seed.length())
                .limit(VERIFICATION_CODE_LENGTH)
                .forEach(integer -> stringBuilder.append(seed.charAt(integer)));
    }
}
