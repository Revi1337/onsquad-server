package revi1337.onsquad.backup.crew.domain;

import static revi1337.onsquad.common.constant.CacheConst.CREW_TOP_USERS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CrewTopMemberCacheRepository {

    private final CrewTopMemberJpaRepository crewTopMemberJpaRepository;

    @Cacheable(cacheNames = CREW_TOP_USERS, key = "'crew:' + #crewId")
    public List<CrewTopMember> findAllByCrewId(Long crewId) {
        return crewTopMemberJpaRepository.findAllByCrewId(crewId);
    }
}
