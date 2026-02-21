package revi1337.onsquad.auth.verification.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.common.application.mail.EmailSender;

@ExtendWith(MockitoExtension.class)
class VerificationMailServiceTest {

    @Mock(name = "verificationCodeEmailSender")
    private EmailSender emailSender;

    @Mock
    private VerificationCodeStorage verificationCodeStorage;

    @Mock
    private VerificationCodeGenerator verificationCodeGenerator;

    @InjectMocks
    private VerificationMailService verificationMailService;

    @Test
    @DisplayName("인증번호 발송 시 저장소에 저장하고 메일을 발송한다")
    void sendVerificationCode() {
        String email = "test@email.com";
        String code = "123456";
        when(verificationCodeGenerator.generate()).thenReturn(code);

        verificationMailService.sendVerificationCode(email);

        verify(verificationCodeStorage).saveVerificationCode(eq(email), eq(code), any(), any());
        verify(emailSender).sendEmail(anyString(), any(), eq(email));
    }

    @Test
    @DisplayName("올바른 코드로 검증 시 SUCCESS로 마킹하고 true를 반환한다")
    void validateVerificationCode1() {
        String email = "test@email.com";
        String authCode = "123456";
        when(verificationCodeStorage.markVerificationStatusAsSuccess(eq(email), eq(authCode), any())).thenReturn(true);

        boolean result = verificationMailService.validateVerificationCode(email, authCode);

        assertThat(result).isTrue();
        verify(verificationCodeStorage).markVerificationStatusAsSuccess(eq(email), eq(authCode), any());
    }

    @Test
    @DisplayName("올바른 코드로 검증 실패 시 SUCCESS로 마킹하지 못하고 false를 반환한다")
    void validateVerificationCode2() {
        String email = "test@email.com";
        String authCode = "123456";
        when(verificationCodeStorage.markVerificationStatusAsSuccess(eq(email), eq(authCode), any())).thenReturn(false);

        boolean result = verificationMailService.validateVerificationCode(email, authCode);

        assertThat(result).isFalse();
        verify(verificationCodeStorage).markVerificationStatusAsSuccess(eq(email), eq(authCode), any());
    }
}
