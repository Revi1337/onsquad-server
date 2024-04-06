package revi1337.onsquad.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Nickname {

    public static final int MIN_LENGTH = 2;
    public static final int MAX_LENGTH = 8;

    @Column(name = "nickname", length = MAX_LENGTH * 3, nullable = false)
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
            throw new IllegalArgumentException("닉네임은 2자 이상 8자 이하여야 합니다.");
        }
    }
}
