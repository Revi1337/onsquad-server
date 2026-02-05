package revi1337.onsquad.common.application.mail;

public interface EmailSender { // TODO PATTERN 이거 Naver 는 잘되는데, GOOGLE 은 깨짐. 다 Table 로 바꿔야 가능할듯..?

    void sendEmail(String subject, EmailContent content, String to);

}
