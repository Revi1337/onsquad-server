package revi1337.onsquad.crew_member.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.crew.domain.vo.Name;

import java.util.Optional;

public interface CrewMemberJpaRepository extends JpaRepository<CrewMember, Long>, CrewMemberQueryRepository {

    @Query("select cm from CrewMember as cm where cm.crew.id = :crewId and cm.member.id = :memberId")
    Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId);

    @Query("select cm from CrewMember as cm inner join fetch cm.member as m where cm.crew.id = :crewId and cm.member.id = :memberId")
    Optional<CrewMember> findWithMemberByCrewIdAndMemberId(Long crewId, Long memberId);

    boolean existsByMemberIdAndCrewId(Long memberId, Long crewId);

}
