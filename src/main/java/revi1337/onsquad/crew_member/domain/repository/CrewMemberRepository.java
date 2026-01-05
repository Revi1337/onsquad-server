package revi1337.onsquad.crew_member.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.result.MyParticipantCrewResult;

public interface CrewMemberRepository {

    Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId);

    boolean existsByMemberIdAndCrewId(Long memberId, Long crewId);

    Page<CrewMember> fetchParticipantsByCrewId(Long crewId, Pageable pageable);

    List<MyParticipantCrewResult> fetchParticipantCrews(Long memberId);

    int deleteByMemberId(Long memberId);

    int deleteByCrewIdIn(List<Long> crewIds);
    
}
