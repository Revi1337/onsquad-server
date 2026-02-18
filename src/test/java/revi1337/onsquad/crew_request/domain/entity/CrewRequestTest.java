package revi1337.onsquad.crew_request.domain.entity;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;

class CrewRequestTest {

    @Test
    void of() {
        Member revi = createRevi(1L);
        Crew crew = createCrew(2L, revi);
        LocalDateTime requestAt = LocalDate.of(2026, 1, 4).atStartOfDay();

        CrewRequest request = CrewRequest.of(crew, revi, requestAt);

        assertSoftly(softly -> {
            softly.assertThat(request.getCrew()).isEqualTo(crew);
            softly.assertThat(request.getMember()).isEqualTo(revi);
            softly.assertThat(request.getRequestAt()).isEqualTo(requestAt);
        });
    }
}
