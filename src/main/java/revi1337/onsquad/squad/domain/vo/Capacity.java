package revi1337.onsquad.squad.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import revi1337.onsquad.squad.error.exception.SquadDomainException;

import static revi1337.onsquad.squad.error.SquadErrorCode.*;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Capacity {

    private static final int MIN_CAPACITY = 2;
    private static final int MAX_CAPACITY = 1000;

    @Column(name = "capacity")
    private int value;

    @Column(name = "remain")
    private int remain;

    public Capacity(int value) {
        validate(value);
        this.value = value;
        this.remain = value;
    }

    public void validate(int value) {
        if (value < MIN_CAPACITY || value > MAX_CAPACITY) {
            throw new SquadDomainException.InvalidCapacitySize(INVALID_CAPACITY_SIZE, MIN_CAPACITY, MAX_CAPACITY);
        }
    }

    public void decreaseRemain() {
        validateLeft();
        this.remain -= 1;
    }

    private void validateLeft() {
        if (this.remain - 1 < 0) {
            throw new SquadDomainException.NotEnoughLeft(NOT_ENOUGH_LEFT);
        }
    }
}