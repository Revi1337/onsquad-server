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

    private final JavaMailSender javaMailSender;

    @Async("sending-verification-code-executor")
    @Override
    public void sendEmail(String subject, String body, String to) {
        MimeMessage mimeMessage = createEmailForm(subject, body, to);
        try {
            javaMailSender.send(mimeMessage);
        } catch (MailException e) {
            log.error("Exception Invoked While Sending Verification Code", e);
        }
    }

    private MimeMessage createEmailForm(String subject, String body, String to) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
        String baseHyperText = buildHyperText(body);
        try {
            mimeMessageHelper.setText(baseHyperText, true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
        } catch (MessagingException e) {
            log.error("Exception Invoked While Create MIME Message", e);
        }
        return mimeMessage;
    }

}
