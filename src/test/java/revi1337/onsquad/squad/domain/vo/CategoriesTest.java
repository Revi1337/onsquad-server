package revi1337.onsquad.squad.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.squad.domain.vo.Category.*;

@DisplayName("카테고리 VO 테스트")
class CategoriesTest {

    @Test
    @DisplayName("존재하지 않는 카테고리를 입력하면 실패한다.")
    public void categoryTest() {
        // given
        String dummyCategory = "dummy";
        String badmintonText = BADMINTON.getText();

        // when && then
        assertThatThrownBy(() -> new Categories(dummyCategory, badmintonText))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("일치하지 않는 카테고리가 존재합니다.");
    }

    @Test
    @DisplayName("정상적인 카테고리를 입력하면 성공한다.")
    public void categoryTest2() {
        // given
        String baseballText = BASEBALL.getText();
        String badmintonText = BADMINTON.getText();

        // when
        Categories categories = new Categories(baseballText, badmintonText);

        // then
        assertThat(categories.getValue()).isEqualTo("야구,배드민턴");
    }

    @Test
    @DisplayName("중복되지 않는 카테고리 5개 이상 입력하면 실패한다. (1)")
    public void categoryTest3() {
        // given & when & then
        assertThatThrownBy(() -> new Categories(BASEBALL, BADMINTON, FISHING, BASKETBALL, HIKING, FUTSAL))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("너무 많은 카테고리가 포함되어 있습니다.");
    }

    @Test
    @DisplayName("중복되지 않는 카테고리 5개 이상 입력하면 실패한다. (2)")
    public void categoryTest4() {
        // given & when & then
        assertThatThrownBy(() -> new Categories(BASEBALL.getText(), BADMINTON.getText(), FISHING.getText(), BASKETBALL.getText(), HIKING.getText(), FUTSAL.getText()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("너무 많은 카테고리가 포함되어 있습니다.");
    }
}