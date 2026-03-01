package revi1337.onsquad.squad.domain.model;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import revi1337.onsquad.squad.domain.error.SquadDomainException;

class SquadCreateSpecTest {

    @Test
    @DisplayName("유효한 인자로 SquadCreateSpec을 생성하면 모든 필드가 VO로 올바르게 변환된다.")
    void createSpecSuccess() {
        String title = "자바 스쿼드 모집";
        String content = "함께 공부할 사람 구합니다.";
        int capacity = 10;
        String addr = "서울시 강남구";
        String detail = "테헤란로 123";

        SquadCreateSpec spec = new SquadCreateSpec(title, content, capacity, addr, detail, List.of(), "kakao", "discord");

        assertSoftly(softly -> {
            softly.assertThat(spec.getTitle().getValue()).isEqualTo(title);
            softly.assertThat(spec.getContent().getValue()).isEqualTo(content);
            softly.assertThat(spec.getCapacity()).isEqualTo(capacity);
            softly.assertThat(spec.getAddress().getValue()).isEqualTo(addr);
            softly.assertThat(spec.getAddress().getDetail()).isEqualTo(detail);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 500, 1000})
    @DisplayName("정원이 경계값(2~1000) 내에 있으면 객체 생성에 성공한다.")
    void capacityBoundarySuccess(int validCapacity) {
        assertThatCode(() -> new SquadCreateSpec("title", "content", validCapacity, "addr", "detail", List.of(), "k", "d"))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 1001})
    @DisplayName("정원이 최소값 미만이거나 최대값을 초과하면 예외가 발생한다.")
    void capacityBoundaryFail(int invalidCapacity) {
        assertThatThrownBy(() -> new SquadCreateSpec("title", "content", invalidCapacity, "addr", "detail", List.of(), "k", "d"))
                .isExactlyInstanceOf(SquadDomainException.InvalidCapacitySize.class);
    }

    @Test
    @DisplayName("Title이나 Content의 제약 조건을 어기는 문자열이 들어오면 내부 VO 생성 시점에 예외가 발생한다.")
    void voValidationFail() {
        String longTitle = "a".repeat(61);

        assertThatThrownBy(() -> new SquadCreateSpec(longTitle, "content", 10, "addr", "detail", List.of(), "k", "d"))
                .isExactlyInstanceOf(SquadDomainException.InvalidTitleLength.class);
    }
}
