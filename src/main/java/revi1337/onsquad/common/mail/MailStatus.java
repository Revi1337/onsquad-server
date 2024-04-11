package revi1337.onsquad.common.mail;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// TODO 1. 회원가입 이메일 전송시, 실패했을때의 로직을 해당 Enum 과 매핑하여 리팩토링하여야 한다.
@Getter
@RequiredArgsConstructor
public enum MailStatus {

    SUCCESS("200");

    private final String text;

    public static String forSuccess() {
        return SUCCESS.getText();
    }
}
