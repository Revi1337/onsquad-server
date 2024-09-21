package revi1337.onsquad.common.mail;

import org.springframework.scheduling.annotation.Async;

public interface EmailSender {

    @Async
    void sendEmail(String subject, String body, String to);

    default String buildBaseHyperText(String content) {
        return new StringBuilder()
                .append("HEADER")
                .append(content)
                .append("FOOTER")
                .toString();
    }
}
