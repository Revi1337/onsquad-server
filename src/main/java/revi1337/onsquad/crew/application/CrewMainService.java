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
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew.domain.repository.CrewStatisticCacheRepository;
import revi1337.onsquad.crew.domain.result.CrewResult;
import revi1337.onsquad.crew.error.CrewErrorCode;
import revi1337.onsquad.crew.error.exception.CrewBusinessException;
import revi1337.onsquad.crew_member.application.CrewMemberAccessPolicy;
import revi1337.onsquad.crew_member.application.CrewTopMemberCacheService;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewTopMember;
import revi1337.onsquad.squad.domain.repository.SquadRepository;
import revi1337.onsquad.squad.domain.result.SquadResult;

@RequiredArgsConstructor
@Service
public class CrewMainService {

    private final CrewMemberAccessPolicy crewMemberAccessPolicy;
    private final CrewAccessPolicy crewAccessPolicy;
    private final CrewTopMemberCacheService crewTopMemberCacheService;
    private final AnnounceCacheService announceCacheService;
    private final CrewRepository crewRepository;
    private final SquadRepository squadRepository;
    private final CrewStatisticCacheRepository crewStatisticRedisRepository;

    public CrewMainResponse fetchMain(Long memberId, Long crewId, Pageable pageable) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        CrewResult result = crewRepository.fetchCrewWithDetailById(crewId)
                .orElseThrow(() -> new CrewBusinessException.NotFound(CrewErrorCode.NOT_FOUND));

        List<AnnounceResult> announces = announceCacheService.getDefaultAnnounces(crewId);
        List<CrewTopMember> topMembers = crewTopMemberCacheService.findAllByCrewId(crewId);
        List<SquadResult> squads = squadRepository.fetchSquadsWithDetailByCrewIdAndCategory(crewId, CategoryType.ALL, pageable);

        return CrewMainResponse.from(crewMember.isOwner(), result, announces, topMembers, squads);
    }

    // TODO 캐시 정합성을 조금 더 올릴 방법을 생각해봐야 한다. 캐싱 된 이후에 추가된 인원 수, 추가된 신청 수, 추가된 스쿼드 수 를 파악(Redis)해 추가해주는 방향을 생각해야한다.
    public CrewStatisticResponse calculateStatistic(Long memberId, Long crewId) {
        CrewMember crewMember = crewMemberAccessPolicy.ensureMemberInCrewAndGet(memberId, crewId);
        crewAccessPolicy.ensureReadStatisticAccessible(crewMember);
        return CrewStatisticResponse.from(crewStatisticRedisRepository.getStatisticById(crewId));
    }
}
