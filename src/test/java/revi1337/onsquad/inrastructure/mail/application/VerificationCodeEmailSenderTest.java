package revi1337.onsquad.inrastructure.mail.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_EMAIL_BODY;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_EMAIL_SUBJECT;
import static revi1337.onsquad.common.fixture.InfrastructureValueFixture.TEST_VERIFICATION_CODE_1;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class VerificationCodeEmailSenderTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private VerificationCodeEmailSender verificationCodeEmailSender;

    @Test
    @DisplayName("이메일 인증코드 발송에 성공한다.")
    void success() throws Exception {
        MimeMessage mockMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mockMessage);

        verificationCodeEmailSender.sendEmail(TEST_EMAIL_SUBJECT, TEST_VERIFICATION_CODE_1, REVI_EMAIL_VALUE);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender).send(captor.capture());
        assertThat(captor.getValue().getAllRecipients()[0].toString()).isEqualTo(REVI_EMAIL_VALUE);
        assertThat(captor.getValue().getSubject()).isEqualTo(TEST_EMAIL_SUBJECT);
        assertThat((String) captor.getValue().getContent()).contains(TEST_EMAIL_BODY);
    }

    @Test
    @DisplayName("MIME 설정 중 예외가 발생하면 인증코드를 발송하지 않는다.")
    void fail1() throws Exception {
        MimeMessage mockMimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        doThrow(new MessagingException())
                .when(mockMimeMessage).setContent(anyString(), anyString());

        verificationCodeEmailSender.sendEmail(TEST_EMAIL_SUBJECT, TEST_VERIFICATION_CODE_1, REVI_EMAIL_VALUE);

        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }
}
