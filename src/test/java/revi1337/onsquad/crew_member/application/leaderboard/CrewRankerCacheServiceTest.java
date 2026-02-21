package revi1337.onsquad.crew_member.application.leaderboard;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.constant.CacheConst.CREW_RANK_MEMBERS;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;
import revi1337.onsquad.member.domain.entity.Member;

@ContextConfiguration(initializers = RedisTestContainerInitializer.class)
class CrewRankerCacheServiceTest extends ApplicationLayerTestSupport {

    @SpyBean
    private CrewRankerRepository crewRankerRepository;

    @Autowired
    private CrewRankerCacheService crewRankerCacheService;

    @Autowired
    @Qualifier("redisCacheManager")
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        Cache cache = cacheManager.getCache(CREW_RANK_MEMBERS);
        if (cache != null) {
            cache.clear();
        }
    }

    @Test
    @DisplayName("동일한 크루 ID로 반복 조회 시, 최초 1회만 DB를 조회하고 이후에는 캐시된 데이터를 반환한다")
    void success() {
        Long crewId = 1L;
        List<CrewRankerCandidate> candidates = List.of(
                createCrewRankerCandidate(crewId, 1, 2, createRevi(1L), LocalDateTime.now()),
                createCrewRankerCandidate(crewId, 2, 1, createAndong(2L), LocalDateTime.now())
        );
        crewRankerRepository.insertBatch(candidates);
        crewRankerCacheService.findAllByCrewId(crewId);

        crewRankerCacheService.findAllByCrewId(crewId);

        verify(crewRankerRepository, times(1)).findAllByCrewId(crewId);
    }

    private static CrewRankerCandidate createCrewRankerCandidate(Long crewId, int rank, long score, Member member, LocalDateTime lastActivityTime) {
        return new CrewRankerCandidate(
                crewId,
                rank,
                score,
                member.getId(),
                member.getNickname().getValue(),
                member.getMbti().name(),
                lastActivityTime
        );
    }
}
