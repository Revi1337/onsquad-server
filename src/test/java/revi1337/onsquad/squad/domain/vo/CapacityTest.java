package revi1337.onsquad.squad.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.squad.error.exception.SquadDomainException;

class CapacityTest {

    @Test
    @DisplayName("스쿼드 인원이 2 ~ 1000 사이면 성공한다.")
    void success() {
        int count = 500;

        Capacity capacity = new Capacity(count);

        assertThat(capacity).isEqualTo(new Capacity(count));
    }

    @Test
    @DisplayName("스쿼드 인원 감소에 성공한다.")
    void success2() {
        int count = 500;
        Capacity capacity = new Capacity(count);

        capacity.decreaseRemain();

        assertThat(capacity.getRemain()).isEqualTo(499);
    }

    @Test
    @DisplayName("스쿼드 인원이 2 미만이면 실패한다.")
    void fail1() {
        int count = 1;

        assertThatThrownBy(() -> new Capacity(count))
                .isExactlyInstanceOf(SquadDomainException.InvalidCapacitySize.class);
    }

    @Test
    @DisplayName("스쿼드 인원이 1000 초과면 실패한다.")
    void fail2() {
        int count = 1001;

        assertThatThrownBy(() -> new Capacity(count))
                .isExactlyInstanceOf(SquadDomainException.InvalidCapacitySize.class);
    }

    @Test
    @DisplayName("스쿼드 자리가 다 차면 실패한다.")
    void fail3() {
        int count = 5;
        Capacity capacity = new Capacity(count);
        capacity.decreaseRemain();
        capacity.decreaseRemain();
        capacity.decreaseRemain();
        capacity.decreaseRemain();
        capacity.decreaseRemain();

        assertThatThrownBy(capacity::decreaseRemain)
                .isExactlyInstanceOf(SquadDomainException.NotEnoughLeft.class);
    }
}
