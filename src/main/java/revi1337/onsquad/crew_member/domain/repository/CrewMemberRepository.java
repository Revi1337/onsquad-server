package revi1337.onsquad.crew_member.domain.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew_member.domain.dto.CrewMemberDomainDto;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;

public interface CrewMemberRepository {

    CrewMember save(CrewMember crewMember);

    CrewMember saveAndFlush(CrewMember crewMember);

    void deleteAllByCrewId(Long crewId);

    Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId);

    Optional<CrewMember> findWithCrewByCrewIdAndMemberId(Long crewId, Long memberId);

    Boolean existsByMemberIdAndCrewId(Long memberId, Long crewId);

    Page<CrewMemberDomainDto> findManagedCrewMembersByCrewId(Long crewId, Pageable pageable);

}
