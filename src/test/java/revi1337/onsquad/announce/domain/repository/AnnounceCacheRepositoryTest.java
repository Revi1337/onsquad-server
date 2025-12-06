package revi1337.onsquad.announce.domain.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCE;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCES;
import static revi1337.onsquad.common.fixture.AnnounceFixture.ANNOUNCE;
import static revi1337.onsquad.common.fixture.AnnounceFixture.ANNOUNCE_1;
import static revi1337.onsquad.common.fixture.AnnounceFixture.ANNOUNCE_2;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.error.exception.AnnounceBusinessException;
import revi1337.onsquad.common.config.PersistenceLayerConfiguration;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@ImportAutoConfiguration(CacheAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({PersistenceLayerConfiguration.class, AnnounceQueryDslRepository.class, AnnounceCacheRepository.class})
@DataJpaTest(showSql = false)
class AnnounceCacheRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private AnnounceJpaRepository announceJpaRepository;

    @SpyBean
    private AnnounceQueryDslRepository announceQueryDslRepository;

    @Autowired
    private AnnounceCacheRepository announceCacheRepository;

    @Test
    @DisplayName("한번 캐싱된 Announce 는 두번다시 호출되지 않는다.")
    void success1() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember CREW_MEMBER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        Announce ANNOUNCE = announceJpaRepository.save(ANNOUNCE(CREW, CREW_MEMBER));
        announceCacheRepository.fetchCacheByCrewIdAndId(CREW.getId(), ANNOUNCE.getId());
        reset(announceQueryDslRepository);

        announceCacheRepository.fetchCacheByCrewIdAndId(CREW.getId(), ANNOUNCE.getId());

        verify(announceQueryDslRepository, never()).fetchByCrewIdAndId(CREW.getId(), ANNOUNCE.getId());
    }

    @Test
    @DisplayName("조회된 Announce 없다면 예외를 던진다.")
    void success2() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        Long DUMMY_ANNOUNCE_ID = 4L;

        assertThatThrownBy(() -> announceCacheRepository.fetchCacheByCrewIdAndId(CREW.getId(), DUMMY_ANNOUNCE_ID))
                .isExactlyInstanceOf(AnnounceBusinessException.NotFoundById.class);
    }

    @Test
    @DisplayName("한번 캐싱된 Announce 들은 두번다시 호출되지 않는다.")
    void success3() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        CrewMember CREW_MEMBER = crewMemberJpaRepository.findByCrewIdAndMemberId(CREW.getId(), REVI.getId()).get();
        announceJpaRepository.saveAll(List.of(ANNOUNCE_1(CREW, CREW_MEMBER), ANNOUNCE_2(CREW, CREW_MEMBER)));
        announceCacheRepository.fetchAllCacheInDefaultByCrewId(CREW.getId());
        reset(announceQueryDslRepository);

        announceCacheRepository.fetchAllCacheInDefaultByCrewId(CREW.getId());

        verify(announceQueryDslRepository, never()).fetchAllInDefaultByCrewId(CREW.getId());
    }

    @Test
    @DisplayName("조회된 Announce 들이 빈 리스트여도 캐싱된다.")
    void success4() {
        Member REVI = memberJpaRepository.save(REVI());
        Crew CREW = crewJpaRepository.save(CREW(REVI));
        announceCacheRepository.fetchAllCacheInDefaultByCrewId(CREW.getId());
        reset(announceQueryDslRepository);

        announceCacheRepository.fetchAllCacheInDefaultByCrewId(CREW.getId());

        verify(announceQueryDslRepository, never()).fetchAllInDefaultByCrewId(CREW.getId());
    }

    @TestConfiguration
    @EnableCaching
    static class TestCacheConfig {

        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(CREW_ANNOUNCE, CREW_ANNOUNCES);
        }
    }
}
