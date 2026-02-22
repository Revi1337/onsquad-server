package revi1337.onsquad.squad.application;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.category.presentation.request.CategoryCondition;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.application.dto.response.SquadResponse;
import revi1337.onsquad.squad.application.dto.response.SquadWithLeaderStateResponse;
import revi1337.onsquad.squad.application.dto.response.SquadWithStatesResponse;
import revi1337.onsquad.squad.domain.SquadPolicy;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.model.SimpleSquad;
import revi1337.onsquad.squad.domain.model.SquadDetail;
import revi1337.onsquad.squad.domain.model.SquadLinkableGroup;
import revi1337.onsquad.squad_category.application.SquadCategoryAccessor;
import revi1337.onsquad.squad_category.domain.model.SquadCategories;
import revi1337.onsquad.squad_member.application.SquadMemberAccessor;
import revi1337.onsquad.squad_member.domain.SquadMemberPolicy;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SquadQueryService {

    private final CrewMemberAccessor crewMemberAccessor;
    private final SquadAccessor squadAccessor;
    private final SquadCategoryAccessor squadCategoryAccessor;
    private final SquadMemberAccessor squadMemberAccessor;

    public SquadWithStatesResponse fetchSquad(Long memberId, Long squadId) {
        Squad squad = squadAccessor.getWithDetailById(squadId);
        Optional<SquadMember> meOpt = squadMemberAccessor.findByMemberIdAndSquadId(memberId, squadId);
        CrewMember meInCrew = crewMemberAccessor.getByMemberIdAndCrewId(memberId, squad.getCrew().getId());
        if (meOpt.isPresent()) {
            SquadMember me = meOpt.get();
            boolean alreadyParticipant = true;
            boolean isLeader = SquadMemberPolicy.isLeader(me);
            boolean canSeeParticipants = true;
            boolean canLeave = SquadPolicy.canLeave(me, squad);
            boolean canDelete = SquadPolicy.canDelete(me, meInCrew);

            return SquadWithStatesResponse.from(alreadyParticipant, isLeader, canSeeParticipants, canLeave, canDelete, squad);
        }

        boolean alreadyParticipant = false;
        Boolean isLeader = null;
        boolean canSeeParticipants = SquadPolicy.canReadParticipants(meInCrew);
        Boolean canLeave = null;
        boolean canDelete = SquadPolicy.canDelete(meInCrew);

        return SquadWithStatesResponse.from(alreadyParticipant, isLeader, canSeeParticipants, canLeave, canDelete, squad);
    }

    public PageResponse<SquadResponse> fetchSquadsByCrewId(Long memberId, Long crewId, CategoryCondition condition, Pageable pageable) {
        crewMemberAccessor.validateMemberInCrew(memberId, crewId);
        Page<SquadDetail> squads = squadAccessor.fetchSquadsWithDetailByCrewIdAndCategory(crewId, condition.categoryType(), pageable);
        SquadLinkableGroup<SquadDetail> squadGroup = new SquadLinkableGroup<>(squads.getContent());
        if (squadGroup.isNotEmpty()) {
            SquadCategories categories = squadCategoryAccessor.fetchCategoriesBySquadIdIn(squadGroup.getSquadIds());
            squadGroup.linkCategories(categories);
        }

        List<SquadResponse> response = squadGroup.stream()
                .map(SquadResponse::from)
                .toList();

        return PageResponse.from(new PageImpl<>(response, squads.getPageable(), squads.getTotalElements()));
    }

    public PageResponse<SquadWithLeaderStateResponse> fetchManageList(Long memberId, Long crewId, Pageable pageable) {
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        SquadPolicy.ensureManageable(me);
        Page<SimpleSquad> squads = squadAccessor.fetchManageList(crewId, pageable);
        SquadLinkableGroup<SimpleSquad> squadGroup = new SquadLinkableGroup<>(squads.getContent());
        if (squadGroup.isNotEmpty()) {
            SquadCategories categories = squadCategoryAccessor.fetchCategoriesBySquadIdIn(squadGroup.getSquadIds());
            squadGroup.linkCategories(categories);
        }

        List<SquadWithLeaderStateResponse> response = squadGroup.stream()
                .map(simpleSquad -> {
                    boolean isLeader = simpleSquad.leader().id().equals(memberId);
                    return SquadWithLeaderStateResponse.from(isLeader, simpleSquad);
                })
                .toList();

        return PageResponse.from(new PageImpl<>(response, squads.getPageable(), squads.getTotalElements()));
    }
}
