package revi1337.onsquad.auth.verification.application;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;
import revi1337.onsquad.auth.verification.domain.error.VerificationException;

@ExtendWith(MockitoExtension.class)
class EmailVerificationValidatorTest {

    @Mock
    private VerificationCodeStorage verificationCodeStorage;

    @InjectMocks
    private EmailVerificationValidator emailVerificationValidator;

    @Test
    @DisplayName("이메일 인증 상태가 SUCCESS가 아니면 예외를 던진다")
    void ensureEmailVerified() {
        String email = "test@email.com";
        doReturn(false).when(verificationCodeStorage).isMarkedVerificationStatusWith(email, VerificationStatus.SUCCESS);

        assertThatThrownBy(() -> emailVerificationValidator.ensureEmailVerified(email))
                .isExactlyInstanceOf(VerificationException.UnAuthenticateVerificationCode.class);
    }

    @Test
    @DisplayName("이메일 인증 상태가 SUCCESS이면 예외 없이 통과한다")
    void ensureEmailVerified2() {
        String email = "test@email.com";
        doReturn(true).when(verificationCodeStorage).isMarkedVerificationStatusWith(email, VerificationStatus.SUCCESS);

        assertThatCode(() -> emailVerificationValidator.ensureEmailVerified(email))
                .doesNotThrowAnyException();
    }
}
