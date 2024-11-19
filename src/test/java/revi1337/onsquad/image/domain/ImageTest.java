package revi1337.onsquad.image.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("대표 이미지 Entity 테스트")
class ImageTest {

    @Test
    @DisplayName("image url 은 null 이면 예외를 던진다.")
    public void constructorTest() {
        // given
        String imageUrl = null;

        // when & then
        assertThatThrownBy(() -> new Image(imageUrl))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("image url 이 null 이 아니면 성공한다.")
    public void constructorTest2() {
        // given
        String imageUrl = "[default image url]";

        // when & then
        assertThat(new Image(imageUrl)).isNotNull();
    }
}