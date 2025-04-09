package revi1337.onsquad.inrastructure.mail.application;

public interface EmailSender {

    void sendEmail(String subject, String body, String to);

    default String buildHyperText(String content) {
        return new StringBuilder()
                .append("HEADER")
                .append(content)
                .append("FOOTER")
                .toString();
    }
}
