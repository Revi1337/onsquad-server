package revi1337.onsquad.crew.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import revi1337.onsquad.announce.application.AnnounceCacheService;
import revi1337.onsquad.announce.domain.result.AnnounceResult;
import revi1337.onsquad.category.domain.entity.vo.CategoryType;
import revi1337.onsquad.crew.application.dto.response.CrewMainResponse;
import revi1337.onsquad.crew.application.dto.response.CrewStatisticResponse;
import revi1337.onsquad.crew.domain.repository.CrewStatisticCacheRepository;
import revi1337.onsquad.crew.domain.result.CrewResult;
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
    private final CrewStatisticCacheRepository crewStatisticRedisRepository;

    public CrewMainResponse fetchMain(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewResult result = crewAccessor.getCrewWithDetailById(crewId);
        List<AnnounceResult> announces = announceCacheService.getDefaultAnnounces(crewId);
        List<CrewRankedMember> topMembers = crewRankedMemberCacheService.findAllByCrewId(crewId);
        SquadLinkableGroup<SquadResult> squads = squadAccessor.fetchSquadsWithDetailByCrewIdAndCategory(crewId, CategoryType.ALL, pageable);

        return CrewMainResponse.from(CrewMemberPolicy.canMangeCrew(crewMember), result, announces, topMembers, squads.values());
    }

    // TODO 캐시 정합성을 조금 더 올릴 방법을 생각해봐야 한다. 캐싱 된 이후에 추가된 인원 수, 추가된 신청 수, 추가된 스쿼드 수 를 파악(Redis)해 추가해주는 방향을 생각해야한다.
    public CrewStatisticResponse calculateStatistic(Long memberId, Long crewId) {
        CrewMember crewMember = crewMemberAccessor.getByMemberIdAndCrewId(memberId, crewId);
        CrewMemberPolicy.ensureReadCrewStatisticAccessible(crewMember);

        return CrewStatisticResponse.from(crewStatisticRedisRepository.getStatisticById(crewId));
    }
}
