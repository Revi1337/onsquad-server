package revi1337.onsquad.crew_member.domain;

public interface CrewMemberQueryRepository {

    boolean existsCrewMember(Long memberId);

    boolean existsParticipantCrewMember(Long memberId);

}
