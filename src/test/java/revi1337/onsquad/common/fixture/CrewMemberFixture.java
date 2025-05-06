package revi1337.onsquad.common.fixture;

import java.time.LocalDateTime;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.member.domain.Member;

public class CrewMemberFixture {

    public static CrewMember GENERAL_CREW_MEMBER() {
        return CrewMember.forGeneral(null, null, LocalDateTime.now());
    }

    public static CrewMember GENERAL_CREW_MEMBER(Crew crew, Member member) {
        return CrewMember.forGeneral(crew, member, LocalDateTime.now());
    }

    public static CrewMember GENERAL_CREW_MEMBER(Crew crew, Member member, LocalDateTime participateAt) {
        return CrewMember.forGeneral(crew, member, participateAt);
    }

    public static CrewMember MANAGER_CREW_MEMBER(Crew crew, Member member) {
        return CrewMember.forGeneral(crew, member, LocalDateTime.now());
    }
}
