package revi1337.onsquad.crew.domain.repository.top;

import static revi1337.onsquad.common.constant.CacheConst.CREW_TOP_USERS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew.domain.entity.CrewTopMember;

@RequiredArgsConstructor
@Repository
public class CrewTopMemberCacheRepository {

    private final CrewTopMemberJpaRepository crewTopMemberJpaRepository;

    @Cacheable(cacheNames = CREW_TOP_USERS, key = "'crew:' + #crewId")
    public List<CrewTopMember> findAllByCrewId(Long crewId) {
        return crewTopMemberJpaRepository.findAllByCrewId(crewId);
    }
}
