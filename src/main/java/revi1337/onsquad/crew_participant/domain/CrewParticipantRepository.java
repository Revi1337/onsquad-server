package revi1337.onsquad.crew_participant.domain;

import static revi1337.onsquad.crew_participant.error.CrewParticipantErrorCode.NEVER_REQUESTED;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew_participant.domain.dto.CrewParticipantRequest;
import revi1337.onsquad.crew_participant.domain.dto.SimpleCrewParticipantRequest;
import revi1337.onsquad.crew_participant.error.exception.CrewParticipantBusinessException;
import revi1337.onsquad.member.domain.Member;

public interface CrewParticipantRepository {

    CrewParticipant save(CrewParticipant crewParticipant);

    CrewParticipant saveAndFlush(CrewParticipant crewParticipant);

    void deleteById(Long id);

    Optional<CrewParticipant> findByCrewIdAndMemberId(Long crewId, Long memberId);

    CrewParticipant upsertCrewParticipant(Crew crew, Member member, LocalDateTime now);

    List<CrewParticipantRequest> fetchAllCrewRequestsByMemberId(Long memberId);

    Page<SimpleCrewParticipantRequest> fetchCrewRequests(Long crewId, Pageable pageable);

    default CrewParticipant getByCrewIdAndMemberId(Long crewId, Long memberId) {
        return findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewParticipantBusinessException.NeverRequested(NEVER_REQUESTED));
    }
}
