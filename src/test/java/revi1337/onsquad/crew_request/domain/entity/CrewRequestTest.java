package revi1337.onsquad.crew_request.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;

class CrewRequestTest {

    @Test
    @DisplayName("크루 참가신청 생성에 성공한다.")
    void newCrewParticipant() {
        Member revi = REVI();
        Crew crew = CREW(revi);
        LocalDateTime now = LocalDateTime.now();

        CrewRequest crewRequest = new CrewRequest(crew, revi, now);

        assertThat(crewRequest.getRequestAt()).isEqualTo(now);
    }
}
