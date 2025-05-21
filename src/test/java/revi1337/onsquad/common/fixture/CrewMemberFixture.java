package revi1337.onsquad.common.fixture;

import static revi1337.onsquad.common.config.FixedTime.CLOCK;

import java.time.LocalDateTime;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.member.domain.Member;

public class CrewMemberFixture {

    public static CrewMember GENERAL_CREW_MEMBER() {
        return CrewMember.forGeneral(null, null, LocalDateTime.now(CLOCK));
    }

    public static CrewMember GENERAL_CREW_MEMBER(Crew crew, Member member) {
        return CrewMember.forGeneral(crew, member, LocalDateTime.now(CLOCK));
    }

    public static CrewMember GENERAL_CREW_MEMBER(Crew crew, Member member, LocalDateTime participateAt) {
        return CrewMember.forGeneral(crew, member, participateAt);
    }

    public static CrewMember MANAGER_CREW_MEMBER(Crew crew, Member member) {
        return CrewMember.forManager(crew, member, LocalDateTime.now(CLOCK));
    }
}
