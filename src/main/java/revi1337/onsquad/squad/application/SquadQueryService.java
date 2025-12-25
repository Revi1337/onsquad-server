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
import revi1337.onsquad.squad.application.dto.response.SquadWithStatesResponse;
import revi1337.onsquad.squad.domain.SquadLinkableGroup;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad.domain.result.SquadResult;
import revi1337.onsquad.squad.domain.result.SquadWithLeaderStateResult;
import revi1337.onsquad.squad.error.SquadBusinessException;
import revi1337.onsquad.squad.error.SquadErrorCode;
import revi1337.onsquad.squad_category.domain.SquadCategories;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryRepository;
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
    private final SquadCategoryRepository squadCategoryRepository;

    public SquadWithStatesResponse fetchSquad(Long memberId, Long squadId) {
        Squad squad = squadRepository.findSquadWithDetailById(squadId).orElseThrow(() -> new SquadBusinessException.NotFound(SquadErrorCode.NOT_FOUND));
        SquadMember squadMember = squadMemberAccessPolicy.ensureMemberInSquadAndGet(memberId, squadId);
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, squad.getCrewId());

        boolean alreadyParticipant = squadAccessPolicy.isParticipant(squadId, squadMember);
        boolean isLeader = squadAccessPolicy.isLeader(squadMember);
        boolean canSeeMembers = squadAccessPolicy.canSeeParticipants(crewMember);

        return SquadWithStatesResponse.from(alreadyParticipant, canSeeMembers, isLeader, squad);
    }

    public List<SquadResponse> fetchSquadsByCrewId(Long memberId, Long crewId, CategoryCondition condition, Pageable pageable) {
        crewMemberAccessPolicy.ensureMemberInCrew(memberId, crewId);
        SquadLinkableGroup<SquadResult> squadGroup = new SquadLinkableGroup<>(
                squadRepository.fetchSquadsWithDetailByCrewIdAndCategory(crewId, condition.categoryType(), pageable));
        if (squadGroup.isNotEmpty()) {
            SquadCategories categories = new SquadCategories(squadCategoryRepository.fetchCategoriesBySquadIdIn(squadGroup.getSquadIds()));
            squadGroup.linkCategories(categories);
        }

        return squadGroup.values().stream()
                .map(SquadResponse::from)
                .toList();
    }

    public List<SquadWithLeaderStateResponse> fetchManageList(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        squadAccessPolicy.ensureSquadManageListAccessible(crewMember);
        SquadLinkableGroup<SquadWithLeaderStateResult> squadGroup = new SquadLinkableGroup<>(squadRepository.fetchManageList(memberId, crewId, pageable));
        if (squadGroup.isNotEmpty()) {
            SquadCategories categories = new SquadCategories(squadCategoryRepository.fetchCategoriesBySquadIdIn(squadGroup.getSquadIds()));
            squadGroup.linkCategories(categories);
        }

        return squadGroup.values().stream()
                .map(SquadWithLeaderStateResponse::from)
                .toList();
    }
}
