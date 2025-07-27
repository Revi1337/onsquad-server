package revi1337.onsquad.inrastructure.mail.application;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VerificationCodeEmailSender implements EmailSender {

    private static final String MIME_SETTING_ERROR_LOG = "MimeMessage 설정 중 예외 발생 - 메일 발송 중단";
    private static final String SEND_VERIFICATION_CODE_ERROR_LOG = "이메일 인증코드 발송 중 예외 발생";
    private static final String ONSQUAD_MAIL_BACKGROUND = "/onsquad/mail/background.jpg";
    private static final String ONSQUAD_PRIMARY_LOGO = "/onsquad/mail/logo-row-primary.png";
    private static final String ONSQUAD_SQUARE_LOGO = "/onsquad/mail/logo-square-primary.png";
    private static final DateTimeFormatter VERIFICATION_CODE_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final JavaMailSender javaMailSender;
    private final String cloudfrontBaseDomain;

    public VerificationCodeEmailSender(
            JavaMailSender javaMailSender,
            @Value("${onsquad.aws.cloud-front.base-domain}") String cloudfrontBaseDomain
    ) {
        this.javaMailSender = javaMailSender;
        this.cloudfrontBaseDomain = cloudfrontBaseDomain;
    }

    @Async("sending-verification-code-executor")
    @Override
    public void sendEmail(String subject, EmailContent content, String to) {
        try {
            MimeMessage mimeMessage = createMimeMessage(subject, content, to);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(MIME_SETTING_ERROR_LOG, e);
        } catch (MailException e) {
            log.error(SEND_VERIFICATION_CODE_ERROR_LOG, e);
        }
    }

    private MimeMessage createMimeMessage(String subject, EmailContent content, String to) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());

        String baseHyperText = buildEmailBody(content);
        mimeMessageHelper.setText(baseHyperText, true);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);

        return mimeMessage;
    }

    @Override
    public String buildEmailBody(EmailContent content) {
        VerificationCode verificationCode = (VerificationCode) content;
        return MessageFormat.format(
                PATTERN,
                cloudfrontBaseDomain + ONSQUAD_MAIL_BACKGROUND,
                cloudfrontBaseDomain + ONSQUAD_PRIMARY_LOGO,
                verificationCode.getContent(),
                verificationCode.getExpiredAt().format(VERIFICATION_CODE_DATETIME_FORMATTER),
                cloudfrontBaseDomain + ONSQUAD_SQUARE_LOGO
        );
    }
}
