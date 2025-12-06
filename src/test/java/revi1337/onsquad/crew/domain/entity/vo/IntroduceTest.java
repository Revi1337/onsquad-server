package revi1337.onsquad.crew.domain.entity.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_INTRODUCE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CHANGED_CREW_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_INTRODUCE;
import static revi1337.onsquad.common.fixture.CrewValueFixture.CREW_INTRODUCE_VALUE;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.error.exception.CrewDomainException;

class IntroduceTest {

    @Test
    @DisplayName("Crew 한줄 소개 생성에 성공한다.")
    void success() {
        Introduce introduce = new Introduce(CREW_INTRODUCE_VALUE);

        assertThat(introduce).isEqualTo(CREW_INTRODUCE);
    }

    @Test
    @DisplayName("Crew 한줄 소개는 null 일 수 없다.")
    void fail1() {
        assertThatThrownBy(() -> new Introduce(null))
                .isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Crew 한줄 소개는 1자 이상 200자 이하여야 한다. (1)")
    void fail2() {
        String value = "";

        assertThatThrownBy(() -> new Introduce(value))
                .isExactlyInstanceOf(CrewDomainException.InvalidIntroduceLength.class);
    }

    @Test
    @DisplayName("Crew 한줄 소개는 1자 이상 200자 이하여야 한다. (2)")
    void fail3() {
        String introduce = "한줄 소개".repeat(40) + "A";

        assertThatThrownBy(() -> new Introduce(introduce))
                .isExactlyInstanceOf(CrewDomainException.InvalidIntroduceLength.class);
    }

    @Test
    @DisplayName("Crew 한줄 소개 업데이트에 성공한다. (1)")
    void successUpdate1() {
        Introduce introduce = new Introduce(CREW_INTRODUCE_VALUE);

        Introduce changedIntroduce = introduce.updateIntroduce(CHANGED_CREW_INTRODUCE_VALUE);

        assertThat(changedIntroduce).isNotSameAs(introduce);
        assertThat(changedIntroduce).isEqualTo(CHANGED_CREW_INTRODUCE);
    }

    @Test
    @DisplayName("Crew 한줄 소개 업데이트에 성공한다. (2)")
    void successUpdate2() {
        Introduce introduce = new Introduce(CREW_INTRODUCE_VALUE);

        Introduce changedIntroduce = introduce.updateIntroduce(CREW_INTRODUCE_VALUE);

        assertThat(changedIntroduce).isNotSameAs(introduce);
        assertThat(changedIntroduce).isEqualTo(CREW_INTRODUCE);
    }
}
