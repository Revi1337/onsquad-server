package revi1337.onsquad.crew_member.domain;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_PARTICIPANT;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew_member.domain.dto.CrewMemberDomainDto;
import revi1337.onsquad.crew_member.domain.dto.EnrolledCrewDomainDto;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;

public interface CrewMemberRepository {

    CrewMember save(CrewMember crewMember);

    CrewMember saveAndFlush(CrewMember crewMember);

    void deleteAllByCrewId(Long crewId);

    Optional<CrewMember> findByCrewIdAndMemberId(Long crewId, Long memberId);

    Boolean existsByMemberIdAndCrewId(Long memberId, Long crewId);

    boolean existsCrewMember(Long memberId);

    boolean existsParticipantCrewMember(Long memberId);

    List<EnrolledCrewDomainDto> fetchEnrolledCrewsByMemberId(Long memberId);

    Page<CrewMemberDomainDto> findManagedCrewMembersByCrewId(Long crewId, Pageable pageable);

    default CrewMember getByCrewIdAndMemberId(Long crewId, Long memberId) {
        return findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(NOT_PARTICIPANT));
    }
}
