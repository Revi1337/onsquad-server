package revi1337.onsquad.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import revi1337.onsquad.member.error.exception.InvalidEmailFormat;
import revi1337.onsquad.member.error.MemberErrorCode;

import java.util.regex.Pattern;

@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Email {

    private static final String EMAIL_REGEX = "^([\\w\\.\\_\\-])*[a-zA-Z0-9]+([\\w\\.\\_\\-])*([a-zA-Z0-9])+([\\w\\.\\_\\-])+@([a-zA-Z0-9]+\\.)+[a-zA-Z0-9]{2,8}$";

    @Column(name = "email")
    private String value;

    public Email(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null) {
            throw new NullPointerException("이메일은 null 일 수 없습니다.");
        }

        if (invalidEmailFormat(value)) {
            throw new InvalidEmailFormat(MemberErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    private boolean invalidEmailFormat(String value) {
        return !Pattern.matches(EMAIL_REGEX, value);
    }
}