package revi1337.onsquad.member.domain.entity.vo;

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

    private static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*\\W).{8,20}$"; // 최소 8자 ~ 20자 (영문 + 특문 + 숫자)
    private static final String BCRYPT_REGEX = "^\\{bcrypt\\}[$]2[abxy]?[$](?:0[4-9]|[12][0-9]|3[01])[$][./0-9a-zA-Z]{53}$"; // BCrypt

    @Column(name = "password", nullable = false)
    private String value;

    private Password(String value) {
        this.value = value;
    }

    public Password update(String value) {
        validateNull(value);
        if (isBcryptFormat(value)) {
            return Password.encrypted(value);
        }
        return Password.raw(value);
    }

    public static Password raw(String value) {
        validateNull(value);
        validatePasswordFormat(value);
        return new Password(value);
    }

    public static Password encrypted(String value) {
        validateNull(value);
        validateBcryptFormat(value);
        return new Password(value);
    }

    private static void validatePasswordFormat(String value) {
        if (invalidPassword(value)) {
            throw new MemberDomainException.InvalidPasswordFormat(INVALID_PASSWORD_FORMAT);
        }
    }

    private static void validateBcryptFormat(String value) {
        if (isNotBcryptFormat(value)) {
            throw new IllegalArgumentException("암호화된 비밀번호는 bcrypt 포맷이어야 합니다.");
        }
    }

    private static boolean invalidPassword(String value) {
        return !Pattern.matches(PASSWORD_REGEX, value);
    }

    private static boolean isBcryptFormat(String value) {
        return Pattern.matches(BCRYPT_REGEX, value);
    }

    private static boolean isNotBcryptFormat(String value) {
        return !isBcryptFormat(value);
    }

    private static void validateNull(String value) {
        if (value == null) {
            throw new NullPointerException("비밀번호는 null 일 수 없습니다.");
        }
    }
}
