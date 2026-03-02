package revi1337.onsquad.squad_member.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.dto.PageResponse;
import revi1337.onsquad.crew.application.CrewAccessor;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.application.SquadAccessor;
import revi1337.onsquad.squad.domain.SquadPolicy;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.model.SquadLinkableGroup;
import revi1337.onsquad.squad_category.application.SquadCategoryAccessor;
import revi1337.onsquad.squad_category.domain.model.SquadCategories;
import revi1337.onsquad.squad_member.application.response.MyParticipantSquadResponse;
import revi1337.onsquad.squad_member.application.response.SquadMemberResponse;
import revi1337.onsquad.squad_member.domain.SquadMemberPolicy;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.model.MyParticipantSquad;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SquadMemberQueryService {

    private final CrewAccessor crewAccessor;
    private final CrewMemberAccessor crewMemberAccessor;
    private final SquadAccessor squadAccessor;
    private final SquadCategoryAccessor squadCategoryAccessor;
    private final SquadMemberAccessor squadMemberAccessor;

    public PageResponse<SquadMemberResponse> fetchParticipants(Long memberId, Long squadId, Pageable pageable) {
        Optional<SquadMember> meOpt = squadMemberAccessor.findByMemberIdAndSquadId(memberId, squadId);
        if (meOpt.isEmpty()) {
            Squad squad = squadAccessor.getById(squadId);
            CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, squad.getCrew().getId());
            SquadPolicy.ensureReadParticipants(me);
            Page<SquadMember> participants = squadMemberAccessor.fetchParticipantsBySquadId(squadId, pageable);
            return resolveParticipantStates(me, participants);
        }

        Page<SquadMember> participants = squadMemberAccessor.fetchParticipantsBySquadId(squadId, pageable);
        return resolveParticipantStates(meOpt.get(), participants);
    }

    public List<MyParticipantSquadResponse> fetchMyParticipatingSquads(Long memberId) {
        List<MyParticipantSquad> squads = squadMemberAccessor.fetchParticipantSquads(memberId);
        SquadLinkableGroup<MyParticipantSquad> squadGroup = new SquadLinkableGroup<>(squads);
        if (squadGroup.isNotEmpty()) {
            SquadCategories categories = squadCategoryAccessor.fetchCategoriesBySquadIdIn(squadGroup.getSquadIds());
            squadGroup.linkCategories(categories);
        }

        Map<Long, List<MyParticipantSquad>> groupedSquads = squadGroup.values().stream().collect(Collectors.groupingBy(MyParticipantSquad::crewId));
        List<Long> crewIds = squads.stream().map(MyParticipantSquad::crewId).toList();

        return crewAccessor.fetchCrewWithStateByIdsIn(crewIds, memberId).stream()
                .map(crewResult -> MyParticipantSquadResponse.from(crewResult, groupedSquads.get(crewResult.crew().id())))
                .toList();
    }

    private PageResponse<SquadMemberResponse> resolveParticipantStates(CrewMember me, Page<SquadMember> participants) {
        Page<SquadMemberResponse> response = participants
                .map(participant -> {
                    Boolean isMe = null;
                    Boolean canKick = null;
                    Boolean canDelegateLeader = null;

                    return SquadMemberResponse.from(isMe, canKick, canDelegateLeader, participant);
                });

        return PageResponse.from(response);
    }

    private PageResponse<SquadMemberResponse> resolveParticipantStates(SquadMember me, Page<SquadMember> participants) {
        Page<SquadMemberResponse> response = participants
                .map(participant -> {
                    boolean isMe = SquadMemberPolicy.isMe(me, participant);
                    boolean canKick = SquadMemberPolicy.canKick(me, participant);
                    boolean canDelegateLeader = SquadMemberPolicy.canDelegateLeader(me, participant);

                    return SquadMemberResponse.from(isMe, canKick, canDelegateLeader, participant);
                });

        return PageResponse.from(response);
    }
}
