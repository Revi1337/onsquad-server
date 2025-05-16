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
import revi1337.onsquad.crew_participant.application.dto.CrewRequestWithCrewDto;
import revi1337.onsquad.crew_participant.application.dto.CrewRequestWithMemberDto;
import revi1337.onsquad.crew_participant.domain.CrewParticipantRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CrewParticipantQueryService {

    private final CrewParticipantRepository crewParticipantRepository;
    private final CrewMemberRepository crewMemberRepository;

    public List<CrewRequestWithMemberDto> fetchAllRequests(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        checkMemberIsCrewOwner(crewMember);

        return crewParticipantRepository.fetchCrewRequests(crewId, pageable).stream()
                .map(CrewRequestWithMemberDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CrewRequestWithCrewDto> fetchAllCrewRequests(Long memberId) {
        return crewParticipantRepository.fetchAllWithSimpleCrewByMemberId(memberId).stream()
                .map(CrewRequestWithCrewDto::from)
                .toList();
    }

    private void checkMemberIsCrewOwner(CrewMember crewMember) {
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(NOT_OWNER);
        }
    }
}
