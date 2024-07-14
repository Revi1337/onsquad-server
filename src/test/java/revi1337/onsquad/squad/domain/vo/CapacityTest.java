package revi1337.onsquad.squad.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.squad.error.exception.SquadDomainException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Capacity VO 테스트")
class CapacityTest {

    @Test
    @DisplayName("Capacity 를 만들때 value 와 remain 이 같다.")
    public void categoryTest() {
        // given
        Capacity capacity = new Capacity(8);

        // when && then
        assertThat(capacity.getValue()).isEqualTo(8);
        assertThat(capacity.getValue()).isEqualTo(capacity.getRemain());
    }

    @Test
    @DisplayName("Capacity 의 최소인원이 맞지않으면 예외를 던진다.")
    public void categoryTest2() {
        // given & when & then
        assertThatThrownBy(() -> new Capacity(1))
                .isInstanceOf(SquadDomainException.InvalidCapacitySize.class)
                .hasMessage(String.format("모집인원은 최소 %d 명 이상 %d 명 이하여야 합니다.", 2, 1000));
    }

    @Test
    @DisplayName("Capacity 의 최대인원이 맞지않으면 예외를 던진다.")
    public void categoryTest3() {
        // given & when & then
        assertThatThrownBy(() -> new Capacity(1001))
                .isInstanceOf(SquadDomainException.InvalidCapacitySize.class)
                .hasMessage(String.format("모집인원은 최소 %d 명 이상 %d 명 이하여야 합니다.", 2, 1000));
    }

    @Test
    @DisplayName("Capacity 의 remain 이 0 에서 더 줄어들으려 하면 예외를 던진다.")
    public void categoryTest4() {
        // given
        Capacity capacity = new Capacity(2);
        capacity.decreaseRemain();
        capacity.decreaseRemain();

        // when && then
        assertThatThrownBy(capacity::decreaseRemain)
                .isInstanceOf(SquadDomainException.NotEnoughLeft.class)
                .hasMessage("정원이 다 찼습니다.");
    }

    @Test
    @DisplayName("Capacity 의 remain 이 정상적을 줄어든다.")
    public void categoryTest5() {
        // given
        Capacity capacity = new Capacity(4);

        // when
        capacity.decreaseRemain();

        // then
        assertThat(capacity.getValue()).isEqualTo(4);
        assertThat(capacity.getRemain()).isEqualTo(3);
    }
}