package revi1337.onsquad.crew_member.domain;

import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.dto.EnrolledCrewMemberDto;

import java.util.List;

public interface CrewMemberQueryRepository {

    List<CrewMember> findEnrolledCrewMembers(Long memberId);

    boolean existsCrewMember(Long memberId);

    List<EnrolledCrewMemberDto> findMembersForSpecifiedCrew(Name crewName, Long memberId);

}
