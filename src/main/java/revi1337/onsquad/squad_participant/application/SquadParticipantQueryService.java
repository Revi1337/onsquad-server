package revi1337.onsquad.squad_participant.application;

import static revi1337.onsquad.squad_member.error.SquadMemberErrorCode.NOT_LEADER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.squad_member.domain.SquadMember;
import revi1337.onsquad.squad_member.domain.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;
import revi1337.onsquad.squad_participant.application.dto.SimpleSquadParticipantDto;
import revi1337.onsquad.squad_participant.application.dto.SquadParticipantRequestDto;
import revi1337.onsquad.squad_participant.domain.SquadParticipantRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadParticipantQueryService {

    private final SquadParticipantRepository squadParticipantRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final SquadMemberRepository squadMemberRepository;

    public List<SquadParticipantRequestDto> fetchAllMyRequests(Long memberId) {
        return squadParticipantRepository.findSquadParticipantRequestsByMemberId(memberId).stream()
                .map(SquadParticipantRequestDto::from)
                .toList();
    }

    public List<SimpleSquadParticipantDto> fetchAllRequests(Long memberId, Long crewId, Long squadId,
                                                            Pageable pageable) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadMember squadMember = squadMemberRepository.getBySquadIdAndCrewMemberId(squadId, crewMember.getId());
        if (squadMember.isNotLeader()) {
            throw new SquadMemberBusinessException.NotLeader(NOT_LEADER);
        }

        return squadParticipantRepository.fetchAllBySquadId(squadId, pageable).stream()
                .map(SimpleSquadParticipantDto::from)
                .toList();
    }
}
