package revi1337.onsquad.crew_member.domain;

import revi1337.onsquad.crew.domain.vo.Name;
import revi1337.onsquad.crew_member.dto.EnrolledCrewMemberDto;

import java.util.List;
import java.util.Optional;

public interface CrewMemberQueryRepository {

    boolean existsCrewMember(Long memberId);

    boolean existsParticipantCrewMember(Long memberId);

    Optional<CrewMember> findCrewMemberByMemberId(Long memberId);

    List<EnrolledCrewMemberDto> findMembersForSpecifiedCrew(Name crewName, Long memberId);

    /**
     * crew_member 테이블에는 crew 와 member 의 pk 를 fk 로 갖고 not null 조건을 갖는다. 그렇기 때문에 left join 이 필요없어 inner join 으로 커스텀한다.
     */
    Optional<CrewMember> findCrewMemberByCrewIdAndMemberId(Long memberId, Long crewId);

}
