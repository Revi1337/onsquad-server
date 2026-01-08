package revi1337.onsquad.member.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.auth.verification.VerificationCodeGenerator;

@TestPropertySource(properties = "spring.mail.code-seed=abcdefghijklmnop")
@ContextConfiguration(classes = VerificationCodeGenerator.class)
@ExtendWith(SpringExtension.class)
class VerificationCodeGeneratorTest {

    @Autowired
    private VerificationCodeGenerator verificationCodeGenerator;

    @Autowired
    private Environment environment;

    @Test
    @DisplayName("메일 인증 코드 길이가 8 이면 성공한다.")
    void success1() {
        String verificationCode = verificationCodeGenerator.generate();

        assertThat(verificationCode).hasSize(8);
    }

    @Test
    @DisplayName("메일 인증 코드 길이의 모든 문자가 Seed 에 포함되면 성공한다.")
    void success2() {
        String verificationSeed = environment.getProperty("spring.mail.code-seed");

        String verificationCode = verificationCodeGenerator.generate();

        assertThat(verificationCode.chars()
                .allMatch(c -> verificationSeed.indexOf(c) >= 0))
                .isTrue();
    }
}
