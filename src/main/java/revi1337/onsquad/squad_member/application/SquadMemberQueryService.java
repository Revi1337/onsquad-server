package revi1337.onsquad.squad_member.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.application.CrewAccessor;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.application.SquadAccessor;
import revi1337.onsquad.squad.domain.SquadPolicy;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad.domain.model.SquadLinkableGroup;
import revi1337.onsquad.squad_category.application.SquadCategoryAccessor;
import revi1337.onsquad.squad_category.domain.model.SquadCategories;
import revi1337.onsquad.squad_member.application.response.MyParticipantResponse;
import revi1337.onsquad.squad_member.application.response.SquadMemberResponse;
import revi1337.onsquad.squad_member.domain.SquadMemberPolicy;
import revi1337.onsquad.squad_member.domain.entity.SquadMember;
import revi1337.onsquad.squad_member.domain.model.MyParticipantSquad;
import revi1337.onsquad.squad_member.error.SquadMemberBusinessException;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SquadMemberQueryService {

    private final CrewAccessor crewAccessor;
    private final CrewMemberAccessor crewMemberAccessor;
    private final SquadAccessor squadAccessor;
    private final SquadCategoryAccessor squadCategoryAccessor;
    private final SquadMemberAccessor squadMemberAccessor;

    public List<SquadMemberResponse> fetchParticipants(Long memberId, Long squadId) {
        Optional<SquadMember> meOpt = squadMemberAccessor.findByMemberIdAndSquadId(memberId, squadId);
        if (meOpt.isPresent()) {
            SquadMember me = meOpt.get();

            return squadMemberAccessor.fetchParticipantsBySquadId(squadId).stream()
                    .map(participant -> resolveParticipantStates(me, participant))
                    .toList();
        }

        Squad squad = squadAccessor.getById(squadId);
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, squad.getCrew().getId());
        if (SquadPolicy.cannotReadParticipants(me)) {
            throw new SquadMemberBusinessException.InsufficientAuthority(SquadMemberErrorCode.INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY);
        }

        return squadMemberAccessor.fetchParticipantsBySquadId(squadId).stream()
                .map(participant -> resolveParticipantStates(me, participant))
                .toList();
    }

    public List<MyParticipantResponse> fetchMyParticipatingSquads(Long memberId) {
        List<MyParticipantSquad> squads = squadMemberAccessor.fetchParticipantSquads(memberId);
        SquadLinkableGroup<MyParticipantSquad> squadGroup = new SquadLinkableGroup<>(squads);
        if (squadGroup.isNotEmpty()) {
            SquadCategories categories = squadCategoryAccessor.fetchCategoriesBySquadIdIn(squadGroup.getSquadIds());
            squadGroup.linkCategories(categories);
        }

        Map<Long, List<MyParticipantSquad>> groupedSquads = squadGroup.values().stream().collect(Collectors.groupingBy(MyParticipantSquad::crewId));
        List<Long> crewIds = squads.stream().map(MyParticipantSquad::crewId).toList();

        return crewAccessor.fetchCrewWithStateByIdsIn(crewIds, memberId).stream()
                .map(crewResult -> MyParticipantResponse.from(crewResult, groupedSquads.get(crewResult.crew().id())))
                .toList();
    }

    private SquadMemberResponse resolveParticipantStates(SquadMember me, SquadMember participant) {
        boolean isMe = SquadMemberPolicy.isMe(me, participant);
        boolean canKick = SquadMemberPolicy.canKick(me, participant);
        boolean canDelegateLeader = SquadMemberPolicy.canDelegateLeader(me, participant);

        return SquadMemberResponse.from(isMe, canKick, canDelegateLeader, participant);
    }

    private SquadMemberResponse resolveParticipantStates(CrewMember me, SquadMember participant) {
        boolean isMe = SquadMemberPolicy.isMe(me, participant);
        boolean canKick = SquadMemberPolicy.canKick(me, participant);
        boolean canDelegateLeader = SquadMemberPolicy.canDelegateLeader(me, participant);

        return SquadMemberResponse.from(isMe, canKick, canDelegateLeader, participant);
    }
}
