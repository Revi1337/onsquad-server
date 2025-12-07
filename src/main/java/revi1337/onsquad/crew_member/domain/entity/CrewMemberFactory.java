package revi1337.onsquad.crew_member.domain.entity;

import static revi1337.onsquad.crew_member.domain.entity.vo.CrewRole.GENERAL;
import static revi1337.onsquad.crew_member.domain.entity.vo.CrewRole.MANAGER;
import static revi1337.onsquad.crew_member.domain.entity.vo.CrewRole.OWNER;

import java.time.LocalDateTime;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.member.domain.entity.Member;

public abstract class CrewMemberFactory {

    public static CrewMember general(Crew crew, Member member, LocalDateTime participantAt) {
        CrewMember crewMember = general(member, participantAt);
        crewMember.addCrew(crew);
        return crewMember;
    }

    public static CrewMember general(Crew crew, Member member) {
        CrewMember crewMember = general(member, LocalDateTime.now());
        crewMember.addCrew(crew);
        return crewMember;
    }

    public static CrewMember general(Member member, LocalDateTime participantAt) {
        return new CrewMember(member, GENERAL, participantAt);
    }

    public static CrewMember manager(Crew crew, Member member, LocalDateTime participantAt) {
        CrewMember crewMember = new CrewMember(crew, member, MANAGER, participantAt);
        crewMember.addCrew(crew);
        return crewMember;
    }

    public static CrewMember owner(Crew crew, Member member, LocalDateTime participantAt) {
        CrewMember crewMember = owner(member, participantAt);
        crewMember.addCrew(crew);
        return crewMember;
    }

    public static CrewMember owner(Member member, LocalDateTime participantAt) {
        return new CrewMember(member, OWNER, participantAt);
    }
}
