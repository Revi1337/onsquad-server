package revi1337.onsquad.crew_participant.application;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_OWNER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.crew_participant.application.dto.CrewParticipantRequestDto;
import revi1337.onsquad.crew_participant.application.dto.SimpleCrewParticipantRequestDto;
import revi1337.onsquad.crew_participant.domain.CrewParticipant;
import revi1337.onsquad.crew_participant.domain.CrewParticipantRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CrewParticipantService {

    private final CrewParticipantRepository crewParticipantRepository;
    private final CrewMemberRepository crewMemberRepository;

    public List<CrewParticipantRequestDto> findMyCrewRequests(Long memberId) {
        return crewParticipantRepository.findMyCrewRequests(memberId).stream()
                .map(CrewParticipantRequestDto::from)
                .toList();
    }

    public void rejectCrewRequest(Long memberId, Long crewId) {
        CrewParticipant crewParticipant = crewParticipantRepository.getByCrewIdAndMemberId(crewId, memberId);
        crewParticipantRepository.deleteById(crewParticipant.getId());
    }

    public List<SimpleCrewParticipantRequestDto> fetchCrewRequests(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(NOT_OWNER);
        }

        return crewParticipantRepository.fetchCrewRequests(crewId, pageable).stream()
                .map(SimpleCrewParticipantRequestDto::from)
                .toList();
    }
}
