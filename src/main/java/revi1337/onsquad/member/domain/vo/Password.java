package revi1337.onsquad.member.domain.vo;

import static revi1337.onsquad.member.error.MemberErrorCode.INVALID_PASSWORD_FORMAT;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.member.error.exception.MemberDomainException;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Password {

    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$"; // 최소 8자 (영문 + 특문 + 숫자)
    private static final String BCRYPT_REGEX = "^[$]2[abxy]?[$](?:0[4-9]|[12][0-9]|3[01])[$][./0-9a-zA-Z]{53}$";
    private static final String BCRYPT_PREFIX = "{bcrypt}";

    @Column(name = "password")
    private String value;

    public Password(String rawPassword) {
        validate(rawPassword);
        this.value = rawPassword;
    }

    public Password(CharSequence encodedPassword) {
        validateNull(encodedPassword);
        if (invalidBcryptFormat(encodedPassword)) {
            throw new IllegalArgumentException("암호화된 비밀번호는 bcrypt 포맷이어야 합니다.");
        }

        this.value = encodedPassword.toString();
    }

    private void validate(CharSequence value) {
        validateNull(value);
        if (invalidPassword(value)) {
            throw new MemberDomainException.InvalidPasswordFormat(INVALID_PASSWORD_FORMAT);
        }
    }

    private void validateNull(CharSequence value) {
        if (value == null) {
            throw new NullPointerException("비밀번호는 null 일 수 없습니다.");
        }
    }

    private boolean invalidPassword(CharSequence value) {
        return !Pattern.matches(PASSWORD_REGEX, value);
    }

    private boolean invalidBcryptFormat(CharSequence encodedPassword) {
        String encodedPasswordString = encodedPassword.toString();
        if (!encodedPasswordString.startsWith(BCRYPT_PREFIX)) {
            return false;
        }

        return !Pattern.matches(BCRYPT_REGEX, encodedPasswordString.substring(8));
    }

    public Password update(CharSequence password) {
        if (password instanceof String castedPassword) {
            return new Password((CharSequence) castedPassword);
        }

        return new Password(password);
    }
}
