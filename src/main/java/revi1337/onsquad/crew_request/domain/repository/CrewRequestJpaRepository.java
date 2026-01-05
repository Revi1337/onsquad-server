package revi1337.onsquad.crew_request.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;

public interface CrewRequestJpaRepository extends JpaRepository<CrewRequest, Long> {

    @Query("select cp from CrewRequest cp where cp.crew.id = :crewId and cp.member.id = :memberId")
    Optional<CrewRequest> findByCrewIdAndMemberId(Long crewId, Long memberId);

    @Transactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete CrewRequest cp where cp.id = :id")
    void deleteById(Long id);

    @Modifying
    @Query("delete CrewRequest cp where cp.member.id = :memberId")
    int deleteByMemberId(Long memberId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete CrewRequest cp where cp.crew.id = :crewId and  cp.member.id = :memberId")
    int deleteByCrewIdAndMemberId(Long crewId, Long memberId);

    @Modifying
    @Query("delete CrewRequest cp where cp.crew.id in :crewIds")
    int deleteByCrewId(List<Long> crewIds);

}
