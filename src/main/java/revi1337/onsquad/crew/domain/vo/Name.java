package revi1337.onsquad.crew.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import revi1337.onsquad.crew.error.exception.CrewDomainException;

import static revi1337.onsquad.crew.error.CrewErrorCode.INVALID_NAME_LENGTH;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Name {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 45;
    private static final int PERSIST_MAX_LENGTH = MAX_LENGTH * 3;

    @Column(name = "name", nullable = false, length = PERSIST_MAX_LENGTH)
    private String value;

    public Name(String value) {
        validate(value);
        this.value = value;
    }

    public void validate(String value) {
        if (value == null) {
            throw new NullPointerException("크루명은 null 일 수 없습니다.");
        }

        if (value.length() > MAX_LENGTH || value.isEmpty()) {
            throw new CrewDomainException.InvalidNameLength(INVALID_NAME_LENGTH, MIN_LENGTH, MAX_LENGTH);
        }
    }

    public Name updateName(String crewName) {
        return new Name(crewName);
    }
}
