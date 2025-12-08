package revi1337.onsquad.squad_request.application;

import static revi1337.onsquad.squad_member.error.SquadMemberErrorCode.NOT_LEADER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;
import revi1337.onsquad.squad_request.application.dto.SquadRequestDto;
import revi1337.onsquad.squad_request.application.dto.SquadRequestWithSquadAndCrewDto;
import revi1337.onsquad.squad_request.domain.repository.SquadRequestRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadRequestQueryService {

    private final SquadRequestRepository squadRequestRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final SquadMemberRepository squadMemberRepository;

    public List<SquadRequestDto> fetchAllRequests(Long memberId, Long crewId, Long squadId, Pageable pageable) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadMember squadMember = squadMemberRepository.getBySquadIdAndCrewMemberId(squadId, crewMember.getId());
        if (squadMember.isNotLeader()) {
            throw new SquadMemberBusinessException.NotLeader(NOT_LEADER);
        }

        return squadRequestRepository.fetchAllBySquadId(squadId, pageable).stream()
                .map(SquadRequestDto::from)
                .toList();
    }

    public List<SquadRequestWithSquadAndCrewDto> fetchAllMyRequests(Long memberId) {
        return squadRequestRepository.findSquadParticipantRequestsByMemberId(memberId).stream()
                .map(SquadRequestWithSquadAndCrewDto::from)
                .toList();
    }
}
