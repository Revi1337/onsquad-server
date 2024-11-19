package revi1337.onsquad.squad.domain.vo;

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
public class Title {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 40;
    private static final int PERSIST_MAX_LENGTH = MAX_LENGTH * 3;

    @Column(name = "title", nullable = false, length = PERSIST_MAX_LENGTH)
    private String value;

    public Title(String value) {
        validate(value);
        this.value = value;
    }

    public void validate(String value) {
        if (value == null) {
            throw new NullPointerException("제목은 null 일 수 없습니다.");
        }

        if (value.length() > MAX_LENGTH || value.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("제목의 길이는 %d 자 이상 %d 자 이하여야 합니다.", MIN_LENGTH, MAX_LENGTH)
            );
        }
    }
}
