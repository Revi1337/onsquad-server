package revi1337.onsquad.common.fixture;

import java.time.LocalDateTime;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.member.domain.entity.Member;

public class CrewParticipantFixture {

    public static CrewRequest CREW_PARTICIPANT(Crew crew, Member member, LocalDateTime requestAt) {
        return new CrewRequest(crew, member, requestAt);
    }

    public static CrewRequest CREW_PARTICIPANT(Crew crew, Member member) {
        return new CrewRequest(crew, member, LocalDateTime.now());
    }
}
