package revi1337.onsquad.crew.application;

import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.announce.application.AnnounceCacheService;
import revi1337.onsquad.announce.application.dto.response.AnnounceWithRoleStateResponse;
import revi1337.onsquad.announce.domain.model.AnnounceDetails;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.crew.application.dto.response.CrewMainResponse;
import revi1337.onsquad.crew.application.dto.response.CrewManageResponse;
import revi1337.onsquad.crew.domain.CrewPolicy;
import revi1337.onsquad.crew.domain.model.CrewDetail;
import revi1337.onsquad.crew.domain.model.CrewStatistic;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.application.leaderboard.CrewRankerCacheService;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.entity.vo.CrewRole;
import revi1337.onsquad.crew_member.domain.model.CrewMembers;
import revi1337.onsquad.squad.application.SquadAccessor;
import revi1337.onsquad.squad.domain.model.SquadDetail;
import revi1337.onsquad.squad.domain.model.SquadLinkableGroup;

@Service
@RequiredArgsConstructor
public class CrewMainService {

    private final CrewAccessor crewAccessor;
    private final CrewMemberAccessor crewMemberAccessor;
    private final CrewRankerCacheService crewRankerCacheService;
    private final AnnounceCacheService announceCacheService;
    private final SquadAccessor squadAccessor;

    public CrewMainResponse fetchMain(Long memberId, Long crewId, Pageable pageable) {
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewDetail crew = crewAccessor.getCrewWithDetailById(crewId);
        List<AnnounceWithRoleStateResponse> announces = getCrewAnnounces(crewId);
        List<CrewRanker> rankedMembers = crewRankerCacheService.findAllByCrewId(crewId);
        SquadLinkableGroup<SquadDetail> squads = squadAccessor.fetchSquadsWithDetailByCrewIdAndCategory(crewId, CategoryType.ALL, pageable);

        boolean canManage = CrewPolicy.canManage(me);

        return CrewMainResponse.from(canManage, crew, announces, rankedMembers, squads.values());
    }

    @Transactional(readOnly = true)
    public CrewManageResponse fetchManageInfo(Long memberId, Long crewId) {
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewPolicy.ensureManageable(me);

        CrewStatistic statistic = crewAccessor.getStatisticById(crewId);
        boolean canModify = CrewPolicy.canModify(me);
        boolean canDelete = CrewPolicy.canDelete(me);

        return CrewManageResponse.from(canModify, canDelete, statistic);
    }

    private List<AnnounceWithRoleStateResponse> getCrewAnnounces(Long crewId) {
        AnnounceDetails announces = new AnnounceDetails(announceCacheService.getDefaultAnnounces(crewId));
        CrewMembers crewMembers = crewMemberAccessor.findAllByCrewIdAndMemberIdIn(crewId, announces.getWriterIds());
        Map<Long, CrewRole> memberRoleMap = crewMembers.splitRolesByMemberId();

        return announces.values().stream()
                .map(detail -> AnnounceWithRoleStateResponse.from(memberRoleMap.get(detail.writer().id()), detail))
                .toList();
    }
}
