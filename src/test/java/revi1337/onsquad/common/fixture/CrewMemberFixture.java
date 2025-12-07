package revi1337.onsquad.common.fixture;

import java.time.LocalDateTime;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;

public class CrewMemberFixture {

    public static CrewMember GENERAL_CREW_MEMBER() {
        return CrewMemberFactory.general(null, null, LocalDateTime.now());
    }

    public static CrewMember GENERAL_CREW_MEMBER(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }

    public static CrewMember GENERAL_CREW_MEMBER(Crew crew, Member member, LocalDateTime participateAt) {
        return CrewMemberFactory.general(crew, member, participateAt);
    }

    public static CrewMember MANAGER_CREW_MEMBER(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }
}
