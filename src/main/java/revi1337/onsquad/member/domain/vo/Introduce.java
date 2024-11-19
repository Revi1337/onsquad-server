package revi1337.onsquad.member.domain.vo;

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
    private static final String DEFAULT_INTRODUCE_VALUE = "[소개 없음]";

    @Column(name = "introduce", length = PERSIST_MAX_LENGTH)
    private String value;

    public Introduce(String value) {
        if (value == null) {
            this.value = DEFAULT_INTRODUCE_VALUE;
            return;
        }

        validateSize(value);
        this.value = value;
    }

    public void validateSize(String value) {
        if (value.length() > MAX_LENGTH || value.isEmpty()) {
            throw new MemberDomainException.InvalidIntroduceLength(INVALID_INTRODUCE_LENGTH, MIN_LENGTH, MAX_LENGTH);
        }
    }

    public Introduce updateIntroduce(String introduce) {
        return new Introduce(introduce);
    }
}
