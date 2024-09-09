package revi1337.onsquad.factory;

import revi1337.onsquad.crew_member.domain.CrewMember;

import static revi1337.onsquad.crew_member.domain.vo.JoinStatus.*;

public class CrewMemberFactory {

    public static CrewMember.CrewMemberBuilder defaultCrewMember() {
        return CrewMember.builder();
    }
}
