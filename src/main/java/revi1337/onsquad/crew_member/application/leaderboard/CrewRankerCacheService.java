package revi1337.onsquad.crew_member.application.leaderboard;

import static revi1337.onsquad.common.constant.CacheConst.CREW_RANK_MEMBERS;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import revi1337.onsquad.crew_member.application.response.CrewRankerResponse;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;

@Service
@RequiredArgsConstructor
public class CrewRankerCacheService {

    private final CrewRankerRepository crewRankerRepository;

    @Cacheable(cacheNames = CREW_RANK_MEMBERS, key = "'crew:' + #crewId", cacheManager = "redisCacheManager")
    public List<CrewRankerResponse> findAllByCrewId(Long crewId) {
        return crewRankerRepository.findAllByCrewId(crewId).stream()
                .map(CrewRankerResponse::from)
                .collect(Collectors.toList());
    }
}
