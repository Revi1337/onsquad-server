package revi1337.onsquad.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import revi1337.onsquad.member.error.exception.MemberDomainException;

import static revi1337.onsquad.member.error.MemberErrorCode.INVALID_NICKNAME_LENGTH;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Nickname {

    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 8;
    private static final int PERSIST_MAX_LENGTH = 8;

    @Column(name = "nickname", length = PERSIST_MAX_LENGTH, nullable = false)
    private String value;

    public Nickname(String value) {
        validate(value);
        this.value = value;
    }

    public void validate(String value) {
        if (value == null) {
            throw new NullPointerException("닉네임은 null 일 수 없습니다.");
        }

        if (value.length() > MAX_LENGTH || value.length() < MIN_LENGTH) {
            throw new MemberDomainException.InvalidNicknameLength(INVALID_NICKNAME_LENGTH, MIN_LENGTH, MAX_LENGTH);
        }
    }
}
