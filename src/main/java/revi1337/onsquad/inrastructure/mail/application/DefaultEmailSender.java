package revi1337.onsquad.inrastructure.mail.application;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultEmailSender implements EmailSender {

    private final JavaMailSender javaMailSender;

    @Override
    public void sendEmail(String subject, String body, String to) {
        MimeMessage emailForm = createEmailForm(subject, body, to);
        try {
            javaMailSender.send(emailForm);
        } catch (RuntimeException e) {
            log.warn("Email Send Exception");
        }
    }

    private MimeMessage createEmailForm(
            String subject,
            String body,
            String to
    ) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                mimeMessage, StandardCharsets.UTF_8.name()
        );
        String baseHyperText = buildBaseHyperText(body);
        try {
            mimeMessageHelper.setText(baseHyperText, true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);
        } catch (MessagingException e) {
            log.warn("Email Create Exception");
        }
        return mimeMessage;
    }

}
