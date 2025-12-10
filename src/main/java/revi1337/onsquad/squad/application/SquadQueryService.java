package revi1337.onsquad.squad.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.presentation.dto.request.CategoryCondition;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberRepository;
import revi1337.onsquad.crew_member.error.CrewMemberErrorCode;
import revi1337.onsquad.crew_member.error.exception.CrewMemberBusinessException;
import revi1337.onsquad.squad.application.dto.SquadDto;
import revi1337.onsquad.squad.application.dto.SquadWithLeaderStateDto;
import revi1337.onsquad.squad.application.dto.SquadWithParticipantAndLeaderAndViewStateDto;
import revi1337.onsquad.squad.domain.dto.SquadDomainDto;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad.error.SquadErrorCode;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException.NotParticipant;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadQueryService {

    private final SquadRepository squadRepository;
    private final CrewMemberRepository crewMemberRepository;
    private final SquadMemberRepository squadMemberRepository;

    public SquadWithParticipantAndLeaderAndViewStateDto fetchSquad(Long memberId, Long crewId, Long squadId) {
        validateSquadBelongToCrew(squadId, crewId);
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId);
        SquadMember squadMember = validateMemberInSquadAndGet(memberId, squadId);
        SquadDomainDto squad = squadRepository.fetchById(squadId)
                .orElseThrow(() -> new SquadBusinessException.NotFound(SquadErrorCode.NOT_FOUND)); // TODO 여기 쿼리 이상할거임. 일단 나중에.

        boolean alreadyParticipant = squadMember != null;
        boolean isLeader = squadMember.isLeader();
        boolean canSeeMembers = crewMember.isOwner();

        return SquadWithParticipantAndLeaderAndViewStateDto.from(alreadyParticipant, canSeeMembers, isLeader, squad);
    }

    public List<SquadDto> fetchSquads(Long memberId, Long crewId, CategoryCondition condition, Pageable pageable) {
        validateMemberInCrew(memberId, crewId);
        return squadRepository.fetchAllByCrewId(crewId, condition.categoryType(), pageable).stream()
                .map(SquadDto::from)
                .toList();
    }

    public List<SquadWithLeaderStateDto> fetchSquadsWithOwnerState(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = validateMemberInCrewAndGet(memberId, crewId);
        if (crewMember.isNotOwner()) {
            throw new CrewMemberBusinessException.NotOwner(CrewMemberErrorCode.NOT_OWNER);
        }
        return squadRepository.fetchAllWithOwnerState(memberId, crewId, pageable).stream()
                .map(SquadWithLeaderStateDto::from)
                .toList();
    }

    private void validateSquadBelongToCrew(Long squadId, Long crewId) { // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        if (!squadRepository.existsByIdAndCrewId(squadId, crewId)) {
            throw new SquadBusinessException.MismatchReference(SquadErrorCode.MISMATCH_CREW_REFERENCE);
        }
    }

    private void validateMemberInCrew(Long memberId, Long crewId) {
        if (crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId).isEmpty()) {
            throw new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT);
        }
    }

    private CrewMember validateMemberInCrewAndGet(Long memberId, Long crewId) {
        return crewMemberRepository.findByCrewIdAndMemberId(crewId, memberId)
                .orElseThrow(() -> new CrewMemberBusinessException.NotParticipant(CrewMemberErrorCode.NOT_PARTICIPANT));
    }

    private SquadMember validateMemberInSquadAndGet(Long memberId, Long squadId) {
        return squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId)
                .orElseThrow(() -> new NotParticipant(SquadMemberErrorCode.NOT_PARTICIPANT));
    }
}
