package revi1337.onsquad.squad.application;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_OWNER;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.presentation.dto.request.CategoryCondition;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.squad.application.dto.SquadInfoDto;
import revi1337.onsquad.squad.application.dto.SquadWithOwnerStateDto;
import revi1337.onsquad.squad.application.dto.SquadWithParticipantStateDto;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.domain.dto.SquadInfoDomainDto;
import revi1337.onsquad.squad_member.domain.SquadMemberRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadQueryService {

    private final SquadRepository squadRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final SquadMemberRepository squadMemberRepository;

    public SquadWithParticipantStateDto fetchSquad(Long memberId, Long crewId, Long squadId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        SquadInfoDomainDto squadInfo = squadRepository.getSquadById(squadId);

        return squadMemberRepository.findBySquadIdAndCrewMemberId(squadId, crewMember.getId())
                .map(squadMember -> SquadWithParticipantStateDto.from(true, squadInfo))
                .orElseGet(() -> SquadWithParticipantStateDto.from(false, squadInfo));
    }

    public List<SquadInfoDto> fetchSquads(Long crewId, CategoryCondition condition, Pageable pageable) {
        return squadRepository.fetchAllByCrewId(crewId, condition.categoryType(), pageable).stream()
                .map(SquadInfoDto::from)
                .toList();
    }

    public List<SquadWithOwnerStateDto> fetchSquadsWithOwnerState(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(NOT_OWNER);
        }

        return squadRepository.fetchAllWithOwnerState(memberId, crewId, pageable).stream()
                .map(SquadWithOwnerStateDto::from)
                .toList();
    }
}
