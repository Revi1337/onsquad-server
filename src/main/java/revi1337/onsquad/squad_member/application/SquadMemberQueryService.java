package revi1337.onsquad.squad_member.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.application.CrewAccessor;
import revi1337.onsquad.crew.domain.result.CrewWithOwnerStateResult;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.domain.CrewMemberPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.application.SquadAccessor;
import revi1337.onsquad.squad.domain.SquadLinkableGroup;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_category.application.SquadCategoryAccessor;
import revi1337.onsquad.squad_category.domain.SquadCategories;
import revi1337.onsquad.squad_member.application.response.MyParticipantResponse;
import revi1337.onsquad.squad_member.application.response.SquadMemberResponse;
import revi1337.onsquad.squad_member.domain.result.MyParticipantSquadResult;
import revi1337.onsquad.squad_member.error.SquadMemberBusinessException;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SquadMemberQueryService {

    private final CrewAccessor crewAccessor;
    private final CrewMemberAccessor crewMemberAccessor;
    private final SquadAccessor squadAccessor;
    private final SquadCategoryAccessor squadCategoryAccessor;
    private final SquadMemberAccessor squadMemberAccessor;

    public List<SquadMemberResponse> fetchParticipants(Long memberId, Long squadId) {
        if (squadMemberAccessor.alreadyParticipant(memberId, squadId)) {
            return fetchMembers(memberId, squadId);
        }

        Squad squad = squadAccessor.getById(squadId);
        CrewMember crewMember = crewMemberAccessor.getByMemberIdAndCrewId(memberId, squad.getCrewId());
        if (CrewMemberPolicy.cannotReadSquadParticipants(crewMember)) {
            throw new SquadMemberBusinessException.InsufficientAuthority(SquadMemberErrorCode.INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY);
        }

        return fetchMembers(memberId, squadId);
    }

    public List<MyParticipantResponse> fetchMyParticipatingSquads(Long memberId) {
        List<MyParticipantSquadResult> squads = squadMemberAccessor.fetchParticipantSquads(memberId);
        SquadLinkableGroup<MyParticipantSquadResult> squadGroup = new SquadLinkableGroup<>(squads);
        if (squadGroup.isNotEmpty()) {
            SquadCategories categories = squadCategoryAccessor.fetchCategoriesBySquadIdIn(squadGroup.getSquadIds());
            squadGroup.linkCategories(categories);
        }

        List<Long> crewIds = squads.stream().map(MyParticipantSquadResult::crewId).toList();
        List<CrewWithOwnerStateResult> crews = crewAccessor.fetchCrewWithStateByIdsIn(crewIds, memberId);
        Map<Long, List<MyParticipantSquadResult>> groupedSquads = squadGroup.values().stream().collect(Collectors.groupingBy(MyParticipantSquadResult::crewId));

        return crews.stream()
                .map(crewResult -> MyParticipantResponse.from(crewResult, groupedSquads.get(crewResult.crew().id())))
                .toList();
    }

    private List<SquadMemberResponse> fetchMembers(Long memberId, Long squadId) {
        return squadMemberAccessor.fetchParticipantsBySquadId(squadId).stream()
                .map(result -> SquadMemberResponse.from(memberId, result))
                .toList();
    }
}
