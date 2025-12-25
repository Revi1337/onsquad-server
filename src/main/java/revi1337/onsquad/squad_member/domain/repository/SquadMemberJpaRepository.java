package revi1337.onsquad.squad_member.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

public interface SquadMemberJpaRepository extends JpaRepository<SquadMember, Long> {

    Optional<SquadMember> findBySquadIdAndMemberId(Long squadId, Long memberId);

    int countBySquadId(Long squadId);

}
