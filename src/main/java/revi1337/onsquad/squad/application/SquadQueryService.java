package revi1337.onsquad.squad.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.presentation.request.CategoryCondition;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.application.dto.response.SquadResponse;
import revi1337.onsquad.squad.application.dto.response.SquadWithLeaderStateResponse;
import revi1337.onsquad.squad.application.dto.response.SquadWithStatesResponse;
import revi1337.onsquad.squad.domain.SquadLinkableGroup;
import revi1337.onsquad.squad.domain.SquadPolicy;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.result.SquadResult;
import revi1337.onsquad.squad.domain.result.SquadWithLeaderStateResult;
import revi1337.onsquad.squad_category.application.SquadCategoryAccessor;
import revi1337.onsquad.squad_category.domain.SquadCategories;
import revi1337.onsquad.squad_member.application.SquadMemberAccessor;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SquadQueryService {

    private final CrewMemberAccessor crewMemberAccessor;
    private final SquadAccessor squadAccessor;
    private final SquadCategoryAccessor squadCategoryAccessor;
    private final SquadMemberAccessor squadMemberAccessor;

    public SquadWithStatesResponse fetchSquad(Long memberId, Long squadId) {
        Squad squad = squadAccessor.getWithDetailById(squadId);
        SquadMember squadMember = squadMemberAccessor.getByMemberIdAndSquadId(memberId, squadId);
        CrewMember crewMember = crewMemberAccessor.getByMemberIdAndCrewId(memberId, squad.getCrewId());

        boolean alreadyParticipant = SquadPolicy.isParticipant(squadId, squadMember);
        boolean isLeader = SquadPolicy.isLeader(squadMember);
        boolean canSeeMembers = SquadPolicy.canSeeParticipants(crewMember);

        return SquadWithStatesResponse.from(alreadyParticipant, canSeeMembers, isLeader, squad);
    }

    public List<SquadResponse> fetchSquadsByCrewId(Long memberId, Long crewId, CategoryCondition condition, Pageable pageable) {
        crewMemberAccessor.validateMemberInCrew(memberId, crewId);
        SquadLinkableGroup<SquadResult> squadGroup = squadAccessor.fetchSquadsWithDetailByCrewIdAndCategory(crewId, condition.categoryType(), pageable);
        if (squadGroup.isNotEmpty()) {
            SquadCategories categories = squadCategoryAccessor.fetchCategoriesBySquadIdIn(squadGroup.getSquadIds());
            squadGroup.linkCategories(categories);
        }

        return squadGroup.values().stream()
                .map(SquadResponse::from)
                .toList();
    }

    public List<SquadWithLeaderStateResponse> fetchManageList(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        SquadPolicy.ensureSquadManageListAccessible(crewMember);
        SquadLinkableGroup<SquadWithLeaderStateResult> squadGroup = squadAccessor.fetchManageList(memberId, crewId, pageable);
        if (squadGroup.isNotEmpty()) {
            SquadCategories categories = squadCategoryAccessor.fetchCategoriesBySquadIdIn(squadGroup.getSquadIds());
            squadGroup.linkCategories(categories);
        }

        return squadGroup.values().stream()
                .map(SquadWithLeaderStateResponse::from)
                .toList();
    }
}
