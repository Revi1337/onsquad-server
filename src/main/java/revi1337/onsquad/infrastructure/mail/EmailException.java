package revi1337.onsquad.infrastructure.mail;

import lombok.Getter;

@Getter
public abstract class EmailException extends RuntimeException {

    public EmailException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class MimeSettingError extends EmailException {

        public MimeSettingError(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class SendError extends EmailException {

        public SendError(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
