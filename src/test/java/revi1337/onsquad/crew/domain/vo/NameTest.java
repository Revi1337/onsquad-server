package revi1337.onsquad.crew.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.error.exception.CrewDomainException;

import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("크루명 vo 테스트")
class NameTest {

    @Test
    @DisplayName("크루명이 null 이면 실패한다.")
    public void crewNameTest() {
        // given
        String name = null;

        // when & then
        assertThatThrownBy(() -> new Name(name))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("크루명은 null 일 수 없습니다.");
    }

    @Test
    @DisplayName("크루명이 null 이 아니면 성공한다.")
    public void crewNameTest2() {
        // given
        String name = "크루명";

        // when
        Name crewName = new Name(name);

        // then
        assertThat(crewName).isNotNull();
    }

    @Test
    @DisplayName("크루명은 15 자가 이하면 성공한다.")
    public void crewNameTest3() {
        // given
        String name = IntStream.of(new Random().ints(15, 67, 90).toArray())
                .mapToObj(integer -> (char) integer)
                .map(String::valueOf)
                .collect(Collectors.joining(""));

        // when
        Name crewName = new Name(name);

        // then
        assertThat(crewName).isNotNull();
    }

    @Test
    @DisplayName("크루명은 45 자를 넘으면 실패한다.")
    public void crewNameTest4() {
        // given
        String name = IntStream.of(new Random().ints(46, 67, 90).toArray())
                .mapToObj(integer -> (char) integer)
                .map(String::valueOf)
                .collect(Collectors.joining(""));

        // when & then
        assertThatThrownBy(() -> new Name(name))
                .isInstanceOf(CrewDomainException.InvalidNameLength.class)
                .hasMessage("크루명의 길이는 1 자 이상 15 자 입니다.");
    }

    @Test
    @DisplayName("크루명이 0 자 이면 넘으면 실패한다.")
    public void crewNameTest5() {
        // given
        String name = "";

        // when & then
        assertThatThrownBy(() -> new Name(name))
                .isInstanceOf(CrewDomainException.InvalidNameLength.class)
                .hasMessage("크루명의 길이는 1 자 이상 15 자 입니다.");
    }
}