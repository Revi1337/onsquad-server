package revi1337.onsquad.crew.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.error.exception.CrewDomainException;

@DisplayName("크루소개 vo 테스트")
class IntroduceTest {

    @Test
    @DisplayName("크루소개는 null 이면 실패한다.")
    public void crewIntroduceTest() {
        // given
        String introduce = null;

        // when & then
        assertThatThrownBy(() -> new Introduce(introduce))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("크루 소개는 null 일 수 없습니다.");
    }

    @Test
    @DisplayName("크루소개는 null 이 아니면 성공한다.")
    public void crewIntroduceTest2() {
        // given
        String introduce = "크루명";

        // when
        Introduce crewIntroduce = new Introduce(introduce);

        // then
        assertThat(crewIntroduce).isNotNull();
    }

    @Test
    @DisplayName("크루소개는 200 자가 이하면 성공한다.")
    public void crewIntroduceTest3() {
        // given
        String introduce = IntStream.of(new Random().ints(200, 67, 90).toArray())
                .mapToObj(integer -> (char) integer)
                .map(String::valueOf)
                .collect(Collectors.joining(""));

        // when
        Introduce crewIntroduce = new Introduce(introduce);

        // then
        assertThat(crewIntroduce).isNotNull();
    }

    @Test
    @DisplayName("크루소개는 200 자를 넘으면 실패한다.")
    public void crewIntroduceTest4() {
        // given
        String introduce = IntStream.of(new Random().ints(201, 67, 90).toArray())
                .mapToObj(integer -> (char) integer)
                .map(String::valueOf)
                .collect(Collectors.joining(""));

        // when & then
        assertThatThrownBy(() -> new Introduce(introduce))
                .isInstanceOf(CrewDomainException.InvalidIntroduceLength.class)
                .hasMessage("크루 소개는 1 자 이상 200 자 입니다.");
    }

    @Test
    @DisplayName("크루소개는 0 자 이면 넘으면 실패한다.")
    public void crewIntroduceTest5() {
        // given
        String introduce = "";

        // when & then
        assertThatThrownBy(() -> new Introduce(introduce))
                .isInstanceOf(CrewDomainException.InvalidIntroduceLength.class)
                .hasMessage("크루 소개는 1 자 이상 200 자 입니다.");
    }
}