package revi1337.onsquad.announce.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import revi1337.onsquad.announce.application.dto.response.AnnounceWithPinAndModifyStateResponse;
import revi1337.onsquad.announce.application.dto.response.AnnouncesWithWriteStateResponse;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.model.AnnounceReference;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

class AnnounceQueryServiceTest extends ApplicationLayerTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private AnnounceRepository announceRepository;

    @Autowired
    private AnnounceQueryService announceQueryService;

    @Nested
    class findAnnounce {

        @Test
        @DisplayName("owner 는 모든 공지사항에 canPin: true, canModify: true")
        void success1() {
            Member revi = memberRepository.save(createRevi());
            Crew crew = crewRepository.save(createCrew(revi));
            Announce announce = announceRepository.save(createCrewAnnounce(crew, revi));

            AnnounceWithPinAndModifyStateResponse response = announceQueryService.findAnnounce(revi.getId(), crew.getId(), announce.getId());

            assertSoftly(softly -> {
                softly.assertThat(response.states().canPin()).isTrue();
                softly.assertThat(response.states().canModify()).isTrue();
            });
        }

        @Test
        @DisplayName("manager 는 자신이 쓴 공지사항에 한해 canPin: false, canModify: true")
        void success2() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, andong));
            Crew savedCrew = crewRepository.save(crew);
            Announce announce = announceRepository.save(createCrewAnnounce(savedCrew, andong));

            AnnounceWithPinAndModifyStateResponse response = announceQueryService.findAnnounce(andong.getId(), crew.getId(), announce.getId());

            assertSoftly(softly -> {
                softly.assertThat(response.states().canPin()).isFalse();
                softly.assertThat(response.states().canModify()).isTrue();
            });
        }

        @Test
        @DisplayName("manager 는 자신보다 높은 등급의 사용자가 작성한 공지사항에 대해 canPin: false, canModify: false")
        void success3() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, andong));
            Crew savedCrew = crewRepository.save(crew);
            Announce announce = announceRepository.save(createCrewAnnounce(savedCrew, revi));

            AnnounceWithPinAndModifyStateResponse response = announceQueryService.findAnnounce(andong.getId(), crew.getId(), announce.getId());

            assertSoftly(softly -> {
                softly.assertThat(response.states().canPin()).isFalse();
                softly.assertThat(response.states().canModify()).isFalse();
            });
        }

        @Test
        @DisplayName("general 은 canPin & canModify 둘다 false")
        void success4() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            Crew savedCrew = crewRepository.save(crew);
            Announce announce = announceRepository.save(createCrewAnnounce(savedCrew, revi));

            AnnounceWithPinAndModifyStateResponse response = announceQueryService.findAnnounce(andong.getId(), crew.getId(), announce.getId());

            assertSoftly(softly -> {
                softly.assertThat(response.states().canPin()).isFalse();
                softly.assertThat(response.states().canModify()).isFalse();
            });
        }
    }

    @Nested
    class findAnnounces {

        @Test
        @DisplayName("owner 는 공지사항을 작성할 수 있고, 리스트를 반환할 수 있다.")
        void success1() {
            Member revi = memberRepository.save(createRevi());
            Crew crew = crewRepository.save(createCrew(revi));
            announceRepository.save(createCrewAnnounce(crew, revi));
            announceRepository.save(createCrewAnnounce(crew, revi));

            AnnouncesWithWriteStateResponse announces = announceQueryService.findAnnounces(revi.getId(), crew.getId());

            assertSoftly(softly -> {
                softly.assertThat(announces.states().canWrite()).isTrue();
                softly.assertThat(announces.announces()).hasSize(2);
            });
        }

        @Test
        @DisplayName("manager 는 공지사항을 작성할 수 있고, 리스트를 반환할 수 있다.")
        void success2() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createManagerCrewMember(crew, andong));
            Crew savedCrew = crewRepository.save(crew);
            announceRepository.save(createCrewAnnounce(savedCrew, revi));
            announceRepository.save(createCrewAnnounce(savedCrew, revi));

            AnnouncesWithWriteStateResponse announces = announceQueryService.findAnnounces(andong.getId(), crew.getId());

            assertSoftly(softly -> {
                softly.assertThat(announces.states().canWrite()).isTrue();
                softly.assertThat(announces.announces()).hasSize(2);
            });
        }

        @Test
        @DisplayName("general 는 공지사항을 작성할 수 없고, 리스트를 반환할 수 있다.")
        void success3() {
            Member revi = memberRepository.save(createRevi());
            Member andong = memberRepository.save(createAndong());
            Crew crew = createCrew(revi);
            crew.addCrewMember(createGeneralCrewMember(crew, andong));
            Crew savedCrew = crewRepository.save(crew);
            announceRepository.save(createCrewAnnounce(savedCrew, revi));
            announceRepository.save(createCrewAnnounce(savedCrew, revi));

            AnnouncesWithWriteStateResponse announces = announceQueryService.findAnnounces(andong.getId(), crew.getId());

            assertSoftly(softly -> {
                softly.assertThat(announces.states().canWrite()).isFalse();
                softly.assertThat(announces.announces()).hasSize(2);
            });
        }
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }

    private Announce createCrewAnnounce(Crew crew, Member revi) {
        String uuid = UUID.randomUUID().toString().substring(0, 10);
        return new Announce(uuid, uuid, crew, revi);
    }

    @TestConfiguration
    static class CacheTestConfig {

        @Bean("redisCacheManager")
        public CacheManager cacheManager() {
            return new NoOpCacheManager();
        }

        @Bean
        public AnnounceCacheEvictor mockAnnounceCacheEvictor() {
            return new AnnounceCacheEvictor() {
                @Override
                public boolean supports(CacheManager cacheManager) {
                    return cacheManager instanceof NoOpCacheManager;
                }

                @Override
                public void evictAnnounce(Long crewId, Long announceId) {

                }

                @Override
                public void evictAnnounces(Long crewId) {

                }

                @Override
                public void evictAnnounces(List<Long> crewIds) {

                }

                @Override
                public void evictAnnouncesByReferences(List<AnnounceReference> references) {

                }

                @Override
                public void evictAnnounceLists(List<Long> crewIds) {

                }
            };
        }
    }
}
