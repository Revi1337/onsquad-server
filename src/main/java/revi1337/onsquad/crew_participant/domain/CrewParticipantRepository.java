package revi1337.onsquad.crew_participant.domain;

import static revi1337.onsquad.crew_participant.error.CrewParticipantErrorCode.NEVER_REQUESTED;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew_participant.domain.dto.CrewRequestWithCrewDomainDto;
import revi1337.onsquad.crew_participant.domain.dto.CrewRequestWithMemberDomainDto;
import revi1337.onsquad.crew_participant.error.exception.CrewParticipantBusinessException;

public interface CrewParticipantRepository {

    CrewParticipant save(CrewParticipant crewParticipant);

    CrewParticipant saveAndFlush(CrewParticipant crewParticipant);

    Optional<CrewParticipant> findById(Long id);

    Optional<CrewParticipant> findWithCrewById(Long id);

    void deleteById(Long id);

    Optional<CrewParticipant> findByCrewIdAndMemberId(Long crewId, Long memberId);

    List<CrewRequestWithCrewDomainDto> fetchAllWithSimpleCrewByMemberId(Long memberId);

    Page<CrewRequestWithMemberDomainDto> fetchCrewRequests(Long crewId, Pageable pageable);

    default CrewParticipant getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new CrewParticipantBusinessException.NeverRequested(NEVER_REQUESTED));
    }

    default CrewParticipant getWithCrewById(Long id) {
        return findWithCrewById(id)
                .orElseThrow(() -> new CrewParticipantBusinessException.NeverRequested(NEVER_REQUESTED));
    }

    default CrewParticipant getByCrewIdAndMemberId(Long crewId, Long memberId) {
        return findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewParticipantBusinessException.NeverRequested(NEVER_REQUESTED));
    }
}
