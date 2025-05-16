package revi1337.onsquad.squad.application;

import static revi1337.onsquad.crew_member.error.CrewMemberErrorCode.NOT_OWNER;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.presentation.dto.request.CategoryCondition;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.squad.application.dto.SquadDto;
import revi1337.onsquad.squad.application.dto.SquadWithLeaderStateDto;
import revi1337.onsquad.squad.application.dto.SquadWithParticipantAndLeaderAndViewStateDto;
import revi1337.onsquad.squad.domain.SquadRepository;
import revi1337.onsquad.squad.domain.dto.SquadDomainDto;
import revi1337.onsquad.squad_member.domain.SquadMember;
import revi1337.onsquad.squad_member.domain.SquadMemberRepository;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadQueryService {

    private final SquadRepository squadRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final SquadMemberRepository squadMemberRepository;

    public SquadWithParticipantAndLeaderAndViewStateDto fetchSquad(Long memberId, Long crewId, Long squadId) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        Optional<SquadMember> squadMember = squadMemberRepository
                .findBySquadIdAndCrewMemberId(squadId, crewMember.getId());
        SquadDomainDto squad = squadRepository.getSquadById(squadId);

        boolean alreadyParticipant = squadMember.isPresent();
        boolean isLeader = squadMember.isPresent() && squadMember.get().isLeader();
        boolean canSeeMembers = squadMember.isPresent() || crewMember.isOwner();

        return SquadWithParticipantAndLeaderAndViewStateDto.from(alreadyParticipant, canSeeMembers, isLeader, squad);
    }

    public List<SquadDto> fetchSquads(Long crewId, CategoryCondition condition, Pageable pageable) {
        return squadRepository.fetchAllByCrewId(crewId, condition.categoryType(), pageable).stream()
                .map(SquadDto::from)
                .toList();
    }

    public List<SquadWithLeaderStateDto> fetchSquadsWithOwnerState(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberRepository.getByCrewIdAndMemberId(crewId, memberId);
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(NOT_OWNER);
        }

        return squadRepository.fetchAllWithOwnerState(memberId, crewId, pageable).stream()
                .map(SquadWithLeaderStateDto::from)
                .toList();
    }
}
