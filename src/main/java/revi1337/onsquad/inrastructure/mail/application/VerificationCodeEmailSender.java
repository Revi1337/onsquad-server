package revi1337.onsquad.inrastructure.mail.application;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class VerificationCodeEmailSender implements EmailSender {

    private static final String MIME_SETTING_ERROR = "MimeMessage 설정 중 예외 발생 - 메일 발송 중단";
    private static final String SEND_VERIFICATION_CODE_ERROR = "이메일 인증코드 발송 중 예외 발생";

    private final JavaMailSender javaMailSender;

    @Async("sending-verification-code-executor")
    @Override
    public void sendEmail(String subject, String body, String to) {
        try {
            MimeMessage mimeMessage = createEmailForm(subject, body, to);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(MIME_SETTING_ERROR, e);
        } catch (MailException e) {
            log.error(SEND_VERIFICATION_CODE_ERROR, e);
        }
    }

    private MimeMessage createEmailForm(String subject, String body, String to) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

        String baseHyperText = buildHyperText(body);
        mimeMessageHelper.setText(baseHyperText, true);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);

        return mimeMessage;
    }
}
