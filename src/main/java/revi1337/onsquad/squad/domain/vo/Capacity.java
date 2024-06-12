package revi1337.onsquad.squad.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@ToString
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Capacity {

    private static final int MIN_CAPACITY = 1;
    private static final int MAX_CAPACITY = 1000;
    private static final String PERSIST_MIN_CAPACITY = MIN_CAPACITY + "";

    @Column(name = "capacity")
    private int value;

    public Capacity(int value) {
        validate(value);
        this.value = value;
    }

    public void validate(int value) {
        if (value < MIN_CAPACITY || value > MAX_CAPACITY) {
            throw new IllegalArgumentException(
                    String.format("모집인원은 최소 %d명 이상 %d명 이하여야 합니다.", MIN_CAPACITY, MAX_CAPACITY)
            );
        }
    }
}