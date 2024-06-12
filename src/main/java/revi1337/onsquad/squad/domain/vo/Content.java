package revi1337.onsquad.squad.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Content {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 300;
    private static final int PERSIST_MAX_LENGTH = MAX_LENGTH * 3;

    @Column(name = "content", nullable = false, length = PERSIST_MAX_LENGTH)
    private String value;

    public Content(String value) {
        validate(value);
        this.value = value;
    }

    public void validate(String value) {
        if (value == null) {
            throw new NullPointerException("내용은 null 일 수 없습니다.");
        }

        if (value.length() > MAX_LENGTH || value.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("내용의 길이는 %d 자 이상 %d 자 이하여야 합니다", MIN_LENGTH, MAX_LENGTH)
            );
        }
    }
}
