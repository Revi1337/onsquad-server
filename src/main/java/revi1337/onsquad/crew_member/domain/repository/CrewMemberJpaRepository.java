package revi1337.onsquad.crew_member.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

public interface CrewMemberJpaRepository extends JpaRepository<CrewMember, Long>, CrewMemberQueryRepository {

    @Query("select cm from CrewMember as cm where cm.crew.id = :crewId and cm.member.id = :memberId")
    Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId);

    @Query("select cm from CrewMember as cm inner join fetch cm.crew where cm.crew.id = :crewId and cm.member.id = :memberId")
    Optional<CrewMember> findWithCrewByCrewIdAndMemberId(Long crewId, Long memberId);

    List<CrewMember> findAllByCrewId(Long crewId);

    boolean existsByMemberIdAndCrewId(Long memberId, Long crewId);

    @Modifying(clearAutomatically = true)
    @Query("delete CrewMember as cm where cm.crew.id = :crewId")
    void deleteAllByCrewId(Long crewId);

}
