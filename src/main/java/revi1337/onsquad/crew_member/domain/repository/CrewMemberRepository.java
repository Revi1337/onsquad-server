package revi1337.onsquad.crew_member.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.result.CrewMemberWithCountResult;
import revi1337.onsquad.crew_member.domain.result.JoinedCrewResult;

public interface CrewMemberRepository {

    CrewMember save(CrewMember crewMember);

    CrewMember saveAndFlush(CrewMember crewMember);

    void deleteAllByCrewId(Long crewId);

    Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId);

    Optional<CrewMember> findWithCrewByCrewIdAndMemberId(Long crewId, Long memberId);

    Boolean existsByMemberIdAndCrewId(Long memberId, Long crewId);

    List<CrewMemberWithCountResult> fetchParticipantsWithCountByCrewId(Long crewId, Pageable pageable);

    List<JoinedCrewResult> fetchJoinedCrewsByMemberId(Long memberId);

}
