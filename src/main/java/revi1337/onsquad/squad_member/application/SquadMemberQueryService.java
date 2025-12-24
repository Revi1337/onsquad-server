package revi1337.onsquad.squad_member.application;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew.domain.result.CrewWithOwnerStateResult;
import revi1337.onsquad.crew_member.application.CrewMemberAccessPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.squad.application.SquadAccessPolicy;
import revi1337.onsquad.squad.domain.SquadLinkableGroup;
import revi1337.onsquad.squad.domain.entity.Squad;
import revi1337.onsquad.squad_category.domain.SquadCategories;
import revi1337.onsquad.squad_category.domain.repository.SquadCategoryRepository;
import revi1337.onsquad.squad_member.application.response.MyParticipantResponse;
import revi1337.onsquad.squad_member.application.response.SquadMemberResponse;
import revi1337.onsquad.squad_member.domain.repository.SquadMemberRepository;
import revi1337.onsquad.squad_member.domain.result.MyParticipantSquadResult;
import revi1337.onsquad.squad_member.error.SquadMemberErrorCode;
import revi1337.onsquad.squad_member.error.exception.SquadMemberBusinessException;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SquadMemberQueryService {

    private final SquadAccessPolicy squadAccessPolicy;
    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
    private final SquadCategoryRepository squadCategoryRepository;
    private final CrewRepository crewRepository;
    private final SquadMemberRepository squadMemberRepository;

    public List<SquadMemberResponse> fetchParticipants(Long memberId, Long squadId) {
        if (squadMemberRepository.findBySquadIdAndMemberId(squadId, memberId).isPresent()) {
            return fetchMembers(memberId, squadId);
        }

        Squad squad = squadAccessPolicy.ensureSquadExistsAndGet(squadId);
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, squad.getCrewId());
        if (crewMemberAccessPolicy.cannotReadSquadParticipants(crewMember)) {
            throw new SquadMemberBusinessException.InsufficientAuthority(SquadMemberErrorCode.INSUFFICIENT_READ_PARTICIPANTS_AUTHORITY);
        }

        return fetchMembers(memberId, squadId);
    }

    public List<MyParticipantResponse> fetchMyParticipatingSquads(Long memberId) {
        List<MyParticipantSquadResult> squads = squadMemberRepository.fetchParticipantSquads(memberId);
        SquadLinkableGroup<MyParticipantSquadResult> squadGroup = new SquadLinkableGroup<>(squads);
        if (squadGroup.isNotEmpty()) {
            SquadCategories categories = new SquadCategories(squadCategoryRepository.fetchCategoriesBySquadIdIn(squadGroup.getSquadIds()));
            squadGroup.linkCategories(categories);
        }

        List<Long> crewIds = squads.stream().map(MyParticipantSquadResult::crewId).toList();
        List<CrewWithOwnerStateResult> crews = crewRepository.fetchCrewWithStateByIdsIn(crewIds, memberId);
        Map<Long, List<MyParticipantSquadResult>> groupedSquads = squadGroup.values().stream().collect(Collectors.groupingBy(MyParticipantSquadResult::crewId));

        return crews.stream()
                .map(crewResult -> MyParticipantResponse.from(crewResult, groupedSquads.get(crewResult.crew().id())))
                .toList();
    }

    private List<SquadMemberResponse> fetchMembers(Long memberId, Long squadId) {
        return squadMemberRepository.fetchParticipantsBySquadId(squadId).stream()
                .map(result -> SquadMemberResponse.from(memberId, result))
                .toList();
    }
}
