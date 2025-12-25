package revi1337.onsquad.crew_member.application;

import static revi1337.onsquad.common.constant.CacheConst.CREW_TOP_USERS;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import revi1337.onsquad.crew_member.domain.entity.CrewTopMember;
import revi1337.onsquad.crew_member.domain.repository.top.CrewTopMemberJpaRepository;

@RequiredArgsConstructor
@Service
public class CrewTopMemberCacheService {

    private final CrewTopMemberJpaRepository crewTopMemberJpaRepository;

    @Cacheable(cacheNames = CREW_TOP_USERS, key = "'crew:' + #crewId")
    public List<CrewTopMember> findAllByCrewId(Long crewId) {
        return crewTopMemberJpaRepository.findAllByCrewId(crewId);
    }
}
