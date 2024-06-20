package revi1337.onsquad.crew.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import revi1337.onsquad.crew.error.exception.CrewDomainException;

import static revi1337.onsquad.crew.error.CrewErrorCode.*;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Introduce {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 200;
    private static final int PERSIST_MAX_LENGTH = MAX_LENGTH * 3;

    @Column(name = "introduce", nullable = false, length = PERSIST_MAX_LENGTH)
    private String value;

    public Introduce(String value) {
        validate(value);
        this.value = value;
    }

    public void validate(String value) {
        if (value == null) {
            throw new NullPointerException("크루 소개는 null 일 수 없습니다.");
        }

        if (value.length() > MAX_LENGTH || value.isEmpty()) {
            throw new CrewDomainException.InvalidIntroduceLength(INVALID_INTRODUCE_LENGTH, MIN_LENGTH, MAX_LENGTH);
        }
    }

    public Introduce updateIntroduce(String introduce) {
        return new Introduce(introduce);
    }
}
