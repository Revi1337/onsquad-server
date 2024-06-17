package revi1337.onsquad.crew_member.domain;

import java.util.List;

public interface CrewMemberQueryRepository {

    List<CrewMember> findEnrolledCrewMembers(Long memberId);

    boolean existsCrewMember(Long memberId);

}
