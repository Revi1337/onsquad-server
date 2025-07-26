package revi1337.onsquad.inrastructure.mail.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.inrastructure.mail.repository.VerificationCodeRepositoryCandidates;
import revi1337.onsquad.inrastructure.mail.support.VerificationCodeGenerator;

@ExtendWith(MockitoExtension.class)
class AuthMailServiceTest {

    private static final Duration VERIFICATION_CODE_TIMEOUT = Duration.ofMinutes(3);
    private static final Duration JOINING_TIMEOUT = Duration.ofMinutes(5);

    @Mock
    private VerificationCodeEmailSender emailSender;

    @Mock
    private VerificationCodeRepositoryCandidates repositoryChain;

    @Mock
    private VerificationCodeGenerator verificationCodeGenerator;

    @InjectMocks
    private AuthMailService authMailService;

    @Nested
    @DisplayName("이메일 인증 코드 발송을 테스트한다.")
    class SendVerificationCode {

        @Test
        @DisplayName("이메일 인증 코드 발송에 성공한다.")
        void success() {
            String testEmail = "email@email.com";
            String verificationCode = "test-verification-code";
            when(verificationCodeGenerator.generate()).thenReturn(verificationCode);
            doNothing().when(emailSender).sendEmail(anyString(), eq(verificationCode), eq(testEmail));

            authMailService.sendVerificationCode(testEmail);

            verify(verificationCodeGenerator).generate();
            verify(emailSender).sendEmail(anyString(), eq(verificationCode), eq(testEmail));
            verify(repositoryChain).saveVerificationCode(testEmail, verificationCode, VERIFICATION_CODE_TIMEOUT);
        }
    }

    @Nested
    @DisplayName("이메일 인증 코드가 유효한지 테스트한다.")
    class IsValidVerificationCode {

        @Test
        @DisplayName("이메일 인증 코드가 유효하면, 인증완료 처리에 성공한다.")
        void success() {
            String testEmail = "email@email.com";
            String verificationCode = "test-verification-code";
            when(repositoryChain.isValidVerificationCode(testEmail, verificationCode)).thenReturn(true);
            when(repositoryChain.markVerificationStatus(testEmail, VerificationStatus.SUCCESS, Duration.ofMinutes(5)))
                    .thenReturn(true);

            boolean valid = authMailService.validateVerificationCode(testEmail, verificationCode);

            verify(repositoryChain).isValidVerificationCode(testEmail, verificationCode);
            verify(repositoryChain)
                    .markVerificationStatus(testEmail, VerificationStatus.SUCCESS, JOINING_TIMEOUT);
            assertThat(valid).isTrue();
        }

        @Test
        @DisplayName("이메일 인증 코드가 유효하지 않으면, 인증완료 처리에 실패한다.")
        void fail() {
            String testEmail = "email@email.com";
            String verificationCode = "test-verification-code";
            when(repositoryChain.isValidVerificationCode(testEmail, verificationCode)).thenReturn(false);

            boolean valid = authMailService.validateVerificationCode(testEmail, verificationCode);

            verify(repositoryChain).isValidVerificationCode(testEmail, verificationCode);
            verify(repositoryChain, never()).markVerificationStatus(eq(testEmail), any(), any());
            assertThat(valid).isFalse();
        }
    }
}
