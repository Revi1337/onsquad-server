package revi1337.onsquad.crew_participant.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.config.FixedTime.CLOCK;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.member.domain.Member;

class CrewParticipantTest {

    @Test
    @DisplayName("크루 참가신청 생성에 성공한다.")
    void newCrewParticipant() {
        Member revi = REVI();
        Crew crew = CREW(revi);
        LocalDateTime now = LocalDateTime.now(CLOCK);

        CrewParticipant crewParticipant = new CrewParticipant(crew, revi, now);

        assertThat(crewParticipant.getRequestAt()).isEqualTo(now);
    }
}