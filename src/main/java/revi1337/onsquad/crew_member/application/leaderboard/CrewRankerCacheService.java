package revi1337.onsquad.crew_member.application.leaderboard;

import static revi1337.onsquad.common.constant.CacheConst.CREW_RANK_MEMBERS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;

@Service
@RequiredArgsConstructor
public class CrewRankerCacheService {

    private final CrewRankerRepository crewRankerRepository;

    @Cacheable(cacheNames = CREW_RANK_MEMBERS, key = "'crew:' + #crewId")
    public List<CrewRanker> findAllByCrewId(Long crewId) {
        return crewRankerRepository.findAllByCrewId(crewId);
    }
}
