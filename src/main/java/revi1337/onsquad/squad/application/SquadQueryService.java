package revi1337.onsquad.squad.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.presentation.request.CategoryCondition;
import revi1337.onsquad.crew_member.application.CrewMemberAccessPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.application.dto.response.SquadResponse;
import revi1337.onsquad.squad.application.dto.response.SquadWithLeaderStateResponse;
import revi1337.onsquad.squad.application.dto.response.SquadWithParticipantAndLeaderAndViewStateResponse;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad.domain.result.SquadResult;
import revi1337.onsquad.squad.error.SquadErrorCode;
import revi1337.onsquad.squad.error.exception.SquadBusinessException;
import revi1337.onsquad.squad_member.application.SquadMemberAccessPolicy;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadQueryService {

    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
    private final SquadMemberAccessPolicy squadMemberAccessPolicy;
    private final SquadAccessPolicy squadAccessPolicy;
    private final SquadRepository squadRepository;

    public SquadWithParticipantAndLeaderAndViewStateResponse fetchSquad(Long memberId, Long crewId, Long squadId) {
        validateSquadBelongToCrew(squadId, crewId);
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        SquadMember squadMember = squadMemberAccessPolicy.ensureMemberInSquadAndGet(memberId, squadId);
        SquadResult squad = squadRepository.fetchById(squadId)
                .orElseThrow(() -> new SquadBusinessException.NotFound(SquadErrorCode.NOT_FOUND)); // TODO 여기 쿼리 이상할거임. 일단 나중에.

        boolean alreadyParticipant = squadMember != null;
        boolean isLeader = squadMember.isLeader();
        boolean canSeeMembers = crewMember.isOwner();

        return SquadWithParticipantAndLeaderAndViewStateResponse.from(alreadyParticipant, canSeeMembers, isLeader, squad);
    }

    public List<SquadResponse> fetchSquads(Long memberId, Long crewId, CategoryCondition condition, Pageable pageable) {
        crewMemberAccessPolicy.ensureMemberInCrew(memberId, crewId);

        return squadRepository.fetchAllByCrewId(crewId, condition.categoryType(), pageable).stream()
                .map(SquadResponse::from)
                .toList();
    }

    public List<SquadWithLeaderStateResponse> fetchSquadsWithOwnerState(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        squadAccessPolicy.ensureSquadListAccessible(crewMember);

        return squadRepository.fetchAllWithOwnerState(memberId, crewId, pageable).stream()
                .map(SquadWithLeaderStateResponse::from)
                .toList();
    }

    private void validateSquadBelongToCrew(Long squadId, Long crewId) { // TODO 이거는 Policy 에서 제외. 어짜피 Squad Query 리팩토링할 때 삭제해야할듯
        if (!squadRepository.existsByIdAndCrewId(squadId, crewId)) {
            throw new SquadBusinessException.MismatchReference(SquadErrorCode.MISMATCH_CREW_REFERENCE);
        }
    }
}
