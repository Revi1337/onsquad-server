package revi1337.onsquad.crew.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("크루 상세정보 vo 테스트")
class DetailTest {

    @Test
    @DisplayName("크루 상세정보가 null 이면 실패한다.")
    public void crewDetailTest() {
        // given
        String details = null;

        // when & then
        assertThatThrownBy(() -> new Detail(details))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("크루 상세정보는 null 일 수 없습니다.");
    }

    @Test
    @DisplayName("크루 상세정보가 null 이 아니면 성공한다.")
    public void crewDetailTest2() {
        // given
        String details = "크루 상세 정보";

        // when
        Detail detail = new Detail(details);

        // then
        assertThat(detail).isNotNull();
    }

    @Test
    @DisplayName("크루 상세정보는 150 자가 이하면 성공한다.")
    public void crewDetailTest3() {
        // given
        String detail = IntStream.of(new Random().ints(150, 67, 90).toArray())
                .mapToObj(integer -> (char) integer)
                .map(String::valueOf)
                .collect(Collectors.joining(""));

        // when
        Detail details = new Detail(detail);

        // then
        assertThat(details).isNotNull();
    }

    @Test
    @DisplayName("크루 상세정보는 150 자를 넘으면 실패한다.")
    public void crewDetailTest4() {
        // given
        String detail = IntStream.of(new Random().ints(151, 67, 90).toArray())
                .mapToObj(integer -> (char) integer)
                .map(String::valueOf)
                .collect(Collectors.joining(""));

        // when & then
        assertThatThrownBy(() -> new Detail(detail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("크루 상세정보의 길이는 1 자 이상 150 자 이하여야 합니다.");
    }

    @Test
    @DisplayName("크루 상세정보가 0 자이면 넘으면 실패한다.")
    public void crewDetailTest5() {
        // given
        String detail = "";

        // when & then
        assertThatThrownBy(() -> new Detail(detail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("크루 상세정보의 길이는 1 자 이상 150 자 이하여야 합니다.");
    }
}