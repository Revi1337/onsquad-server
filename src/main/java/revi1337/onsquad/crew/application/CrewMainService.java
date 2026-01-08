package revi1337.onsquad.crew.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import revi1337.onsquad.announce.application.AnnounceCacheService;
import revi1337.onsquad.announce.domain.result.AnnounceResult;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.crew.application.dto.response.CrewMainResponse;
import revi1337.onsquad.crew.application.dto.response.CrewManageResponse;
import revi1337.onsquad.crew.domain.repository.CrewStatisticQueryDslRepository;
import revi1337.onsquad.crew.domain.result.CrewResult;
import revi1337.onsquad.crew.domain.result.CrewStatisticResult;
import revi1337.onsquad.crew_member.application.CrewMemberAccessor;
import revi1337.onsquad.crew_member.application.CrewRankedMemberCacheService;
import revi1337.onsquad.crew_member.domain.CrewMemberPolicy;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewRankedMember;
import revi1337.onsquad.squad.application.SquadAccessor;
import revi1337.onsquad.squad.domain.SquadLinkableGroup;
import revi1337.onsquad.squad.domain.result.SquadResult;

@RequiredArgsConstructor
@Service
public class CrewMainService {

    private final CrewAccessor crewAccessor;
    private final CrewMemberAccessor crewMemberAccessor;
    private final CrewRankedMemberCacheService crewRankedMemberCacheService;
    private final AnnounceCacheService announceCacheService;
    private final SquadAccessor squadAccessor;
    private final CrewStatisticQueryDslRepository crewStatisticQueryDslRepository;

    public CrewMainResponse fetchMain(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewResult result = crewAccessor.getCrewWithDetailById(crewId);
        List<AnnounceResult> announces = announceCacheService.getDefaultAnnounces(crewId);
        List<CrewRankedMember> topMembers = crewRankedMemberCacheService.findAllByCrewId(crewId);
        SquadLinkableGroup<SquadResult> squads = squadAccessor.fetchSquadsWithDetailByCrewIdAndCategory(crewId, CategoryType.ALL, pageable);

        boolean canManage = CrewMemberPolicy.canMangeCrew(crewMember);

        return CrewMainResponse.from(canManage, result, announces, topMembers, squads.values());
    }

    public CrewManageResponse fetchManageInfo(Long memberId, Long crewId) {
        CrewMember me = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewMemberPolicy.ensureCanManagementCrew(me);

        CrewStatisticResult result = crewStatisticQueryDslRepository.getStatisticById(crewId);
        boolean canModify = CrewMemberPolicy.canModifyCrew(me);
        boolean canDelete = CrewMemberPolicy.canDeleteCrew(me);

        return CrewManageResponse.from(canModify, canDelete, result);
    }
}
