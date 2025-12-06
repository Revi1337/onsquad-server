package revi1337.onsquad.member.domain.entity.vo;

import static revi1337.onsquad.member.error.MemberErrorCode.INVALID_INTRODUCE_LENGTH;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import revi1337.onsquad.member.error.exception.MemberDomainException;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Introduce {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 200;
    private static final int PERSIST_MAX_LENGTH = MAX_LENGTH * 3;
    private static final String DEFAULT_VALUE = "소개 없음";

    @Column(name = "introduce", length = PERSIST_MAX_LENGTH)
    private String value;

    public Introduce(String value) {
        validateNull(value);
        validateSize(value);
        this.value = value;
    }

    public static Introduce defaultValue() {
        return new Introduce(DEFAULT_VALUE);
    }

    public void validateNull(String value) {
        if (value == null) {
            throw new NullPointerException("자기소개는 null 일 수 없습니다");
        }
    }

    public void validateSize(String value) {
        if (value.length() > MAX_LENGTH || value.isEmpty()) {
            throw new MemberDomainException.InvalidIntroduceLength(INVALID_INTRODUCE_LENGTH);
        }
    }

    public Introduce update(String value) {
        return new Introduce(value);
    }
}
