package revi1337.onsquad.squad.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("제목 VO 테스트")
class TitleTest {

    @Test
    @DisplayName("제목의 길이가 1 ~ 40 이하면 성공한다.")
    public void titleTest() {
        // given
        String testTitle = "테스트 제목";

        // when
        Title title = new Title(testTitle);

        // then
        assertThat(title.getValue()).isEqualTo(testTitle);
    }

    @Test
    @DisplayName("제목의 비어있으면 실패한다.")
    public void titleTest2() {
        // given
        String testTitle = "";

        // when && then
        assertThatThrownBy(() -> new Title(testTitle))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제목의 길이는 1 자 이상 40 자 이하여야 합니다.");
    }

    @Test
    @DisplayName("제목의 40 자가 넘으면 실패한다.")
    public void titleTest3() {
        // given
        String testTitle = "머머머머머머머머머머머머머머머머머머머머머머머머머머머머머머머머머머머머머머머머머";

        // when && then
        assertThatThrownBy(() -> new Title(testTitle))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제목의 길이는 1 자 이상 40 자 이하여야 합니다.");
    }
}