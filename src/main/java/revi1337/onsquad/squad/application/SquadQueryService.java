package revi1337.onsquad.squad.application;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.presentation.request.CategoryCondition;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.domain.CrewMemberPolicy;
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
import revi1337.onsquad.squad_member.domain.SquadMemberPolicy;
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
        Optional<SquadMember> meOpt = squadMemberAccessor.findByMemberIdAndSquadId(memberId, squadId);
        CrewMember meInCrew = crewMemberAccessor.getByMemberIdAndCrewId(memberId, squad.getCrewId());
        if (meOpt.isPresent()) {
            SquadMember me = meOpt.get();
            boolean alreadyParticipant = true;
            boolean isLeader = SquadMemberPolicy.isLeader(me);
            boolean canSeeParticipants = true;
            boolean canLeave = SquadMemberPolicy.canLeaveSquad(me, squad);
            boolean canDelete = SquadPolicy.canDelete(me, meInCrew);

            return SquadWithStatesResponse.from(alreadyParticipant, isLeader, canSeeParticipants, canLeave, canDelete, squad);
        }

        boolean alreadyParticipant = false;
        Boolean isLeader = null;
        boolean canSeeParticipants = CrewMemberPolicy.canReadSquadParticipants(meInCrew);
        Boolean canLeave = null;
        boolean canDelete = SquadPolicy.canDelete(meInCrew);

        return SquadWithStatesResponse.from(alreadyParticipant, isLeader, canSeeParticipants, canLeave, canDelete, squad);
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
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        SquadPolicy.ensureSquadManageListAccessible(me);
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
