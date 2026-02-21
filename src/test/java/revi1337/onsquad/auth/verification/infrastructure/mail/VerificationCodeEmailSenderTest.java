package revi1337.onsquad.auth.verification.infrastructure.mail;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import revi1337.onsquad.auth.verification.domain.VerificationCode;
import revi1337.onsquad.auth.verification.domain.VerificationStatus;

@ExtendWith(MockitoExtension.class)
class VerificationCodeEmailSenderTest {

    private final String cloudfrontDomain = "https://cdn.onsquad.com";

    @Mock
    private JavaMailSender javaMailSender;

    private VerificationCodeEmailSender emailSender;

    @BeforeEach
    void setUp() {
        emailSender = new VerificationCodeEmailSender(javaMailSender, cloudfrontDomain);
    }

    @Test
    @DisplayName("이메일 발송 시 본문(HTML)이 템플릿에 맞춰 정상적으로 생성되어 전달된다")
    void testComputedEmailBody() throws Exception {
        MimeMessage mockMimeMessage = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        VerificationCode code = new VerificationCode(
                "test@test.com", "123456", VerificationStatus.PENDING,
                System.currentTimeMillis() + 600000
        );

        emailSender.sendEmail("제목", code, "test@test.com");

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(javaMailSender).send(captor.capture());
        String sentContent = (String) captor.getValue().getContent();
        assertSoftly(softly -> {
            softly.assertThat(sentContent).contains(cloudfrontDomain + "/onsquad/mail/background.jpg");
            softly.assertThat(sentContent).contains("123456");
        });
    }
}
