package revi1337.onsquad.announce.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCE;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCES;
import static revi1337.onsquad.common.fixture.AnnounceFixture.ANNOUNCE;
import static revi1337.onsquad.common.fixture.AnnounceFixture.FIXED_ANNOUNCE;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_CONTENT_VALUE;
import static revi1337.onsquad.common.fixture.AnnounceValueFixture.ANNOUNCE_TITLE_VALUE;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.GENERAL_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.MANAGER_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.MemberFixtures.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixtures.KWANGWON;
import static revi1337.onsquad.common.fixture.MemberFixtures.REVI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.event.ApplicationEvents;
import revi1337.onsquad.announce.application.dto.AnnounceCreateDto;
import revi1337.onsquad.announce.application.event.AnnounceCreateEvent;
import revi1337.onsquad.announce.application.event.AnnounceFixedEvent;
import revi1337.onsquad.announce.domain.Announce;
import revi1337.onsquad.announce.domain.AnnounceCacheRepository;
import revi1337.onsquad.announce.domain.AnnounceJpaRepository;
import revi1337.onsquad.announce.domain.AnnounceRepository;
import revi1337.onsquad.announce.error.exception.AnnounceBusinessException;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.crew.domain.Crew;
import revi1337.onsquad.crew.domain.CrewRepository;
import revi1337.onsquad.crew_member.domain.CrewMember;
import revi1337.onsquad.crew_member.domain.CrewMemberRepository;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;

class AnnounceCommandServiceTest extends ApplicationLayerTestSupport {

    @SpyBean
    private MemberRepository memberRepository;

    @SpyBean
    private CrewRepository crewRepository;

    @SpyBean
    private CrewMemberRepository crewMemberRepository;

    @SpyBean
    private AnnounceRepository announceRepository;

    @SpyBean
    private AnnounceJpaRepository announceJpaRepository;

    @Autowired
    private ApplicationEvents events;

    @Autowired
    private CacheManager concurrentMapCacheManager;

    @Autowired
    private AnnounceCacheRepository announceCacheRepository;

    @Autowired
    private AnnounceCommandService announceCommandService;

    @BeforeEach
    void setUp() {
        concurrentMapCacheManager.getCache(CREW_ANNOUNCE).clear();
        concurrentMapCacheManager.getCache(CREW_ANNOUNCES).clear();
    }

    @Nested
    @DisplayName("Announce 생성을 테스트한다.")
    class Create {

        @Test
        @DisplayName("Announce 생성에 성공한다.")
        void success() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            AnnounceCreateDto DTO = new AnnounceCreateDto(ANNOUNCE_TITLE_VALUE, ANNOUNCE_CONTENT_VALUE);

            Long ANNOUNCE_ID = announceCommandService.newAnnounce(REVI.getId(), CREW.getId(), DTO);

            assertThat(announceJpaRepository.findById(ANNOUNCE_ID)).isPresent();
            assertThat(events.stream(AnnounceCreateEvent.class)).hasSize(1);
        }

        @Test
        @DisplayName("Crew 에 속한 사용자가 매니저 혹은 크루장이 아니면 실패한다.")
        void fail() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            Member ANDONG = memberRepository.save(ANDONG());
            crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));
            AnnounceCreateDto DTO = new AnnounceCreateDto(ANNOUNCE_TITLE_VALUE, ANNOUNCE_CONTENT_VALUE);

            assertThatThrownBy(() -> announceCommandService.newAnnounce(ANDONG.getId(), CREW.getId(), DTO))
                    .isExactlyInstanceOf(AnnounceBusinessException.CantMake.class);
            assertThat(events.stream(AnnounceCreateEvent.class)).hasSize(0);
        }
    }

    @Nested
    @DisplayName("Announce 상단 고정을 테스트한다.")
    class Fixed {

        @Test
        @DisplayName("Announce 상단 고정에 성공한다.")
        void success1() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberRepository.getByCrewIdAndMemberId(CREW.getId(), REVI.getId());
            Announce ANNOUNCE = announceJpaRepository.save(ANNOUNCE(CREW, CREW_OWNER));

            announceCommandService.fixOrUnfixAnnounce(CREW_OWNER.getId(), CREW.getId(), ANNOUNCE.getId(), true);

            assertThat(events.stream(AnnounceFixedEvent.class)).hasSize(1);
            assertThat(announceRepository.getByIdAndCrewId(ANNOUNCE.getId(), CREW.getId()).isFixed()).isTrue();
            assertThat(announceCacheRepository.fetchCacheByCrewIdAndId(CREW.getId(), ANNOUNCE.getId()).fixed())
                    .isTrue();
        }

        @Test
        @DisplayName("크루 Owner 는 다른 사람이 작성한 Announce 를 상단 고정시킬 수 있다.")
        void success2() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            Member ANDONG = memberRepository.save(ANDONG());
            CrewMember CREW_MANAGER = crewMemberRepository.save(MANAGER_CREW_MEMBER(CREW, ANDONG));
            Announce ANNOUNCE = announceJpaRepository.save(ANNOUNCE(CREW, CREW_MANAGER));
            CrewMember CREW_OWNER = crewMemberRepository.getByCrewIdAndMemberId(CREW.getId(), REVI.getId());

            announceCommandService.fixOrUnfixAnnounce(CREW_OWNER.getId(), CREW.getId(), ANNOUNCE.getId(), true);

            assertThat(events.stream(AnnounceFixedEvent.class)).hasSize(1);
            assertThat(announceRepository.getByIdAndCrewId(ANNOUNCE.getId(), CREW.getId()).isFixed()).isTrue();
            assertThat(announceCacheRepository
                    .fetchCacheByCrewIdAndId(CREW.getId(), ANNOUNCE.getId()).fixed())
                    .isTrue();
        }

        @Test
        @DisplayName("이미 Announce 가 상단 고정이 되어있으면 무시된다.")
        void success3() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            Member ANDONG = memberRepository.save(ANDONG());
            CrewMember CREW_MANAGER = crewMemberRepository.save(MANAGER_CREW_MEMBER(CREW, ANDONG));
            Announce ANNOUNCE = announceJpaRepository.save(FIXED_ANNOUNCE(CREW, CREW_MANAGER));
            CrewMember CREW_OWNER = crewMemberRepository.getByCrewIdAndMemberId(CREW.getId(), REVI.getId());

            announceCommandService.fixOrUnfixAnnounce(CREW_OWNER.getId(), CREW.getId(), ANNOUNCE.getId(), true);

            assertThat(events.stream(AnnounceFixedEvent.class)).hasSize(0);
            verify(announceRepository, never()).saveAndFlush(any(Announce.class));
            assertThat(announceRepository.getByIdAndCrewId(ANNOUNCE.getId(), CREW.getId()).isFixed()).isTrue();
        }

        @Test
        @DisplayName("크루 일반 사용자는 Announce 상단 고정에 실패한다.")
        void fail1() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            Member KWANGWON = memberRepository.save(KWANGWON());
            CrewMember CREW_MANAGER = crewMemberRepository.save(MANAGER_CREW_MEMBER(CREW, KWANGWON));
            Announce ANNOUNCE = announceJpaRepository.save(ANNOUNCE(CREW, CREW_MANAGER));
            Member ANDONG = memberRepository.save(ANDONG());
            CrewMember CREW_GENERAL = crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));

            assertThatThrownBy(() -> announceCommandService
                    .fixOrUnfixAnnounce(CREW_GENERAL.getId(), CREW.getId(), ANNOUNCE.getId(), true))
                    .isExactlyInstanceOf(AnnounceBusinessException.CantFix.class);
        }

        @Test
        @DisplayName("크루 매니저는 Announce 상단 고정에 실패한다.")
        void fail2() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            Member KWANGWON = memberRepository.save(KWANGWON());
            CrewMember CREW_MANAGER = crewMemberRepository.save(MANAGER_CREW_MEMBER(CREW, KWANGWON));
            Announce ANNOUNCE = announceJpaRepository.save(ANNOUNCE(CREW, CREW_MANAGER));

            assertThatThrownBy(() -> announceCommandService
                    .fixOrUnfixAnnounce(CREW_MANAGER.getId(), CREW.getId(), ANNOUNCE.getId(), true))
                    .isExactlyInstanceOf(AnnounceBusinessException.CantFix.class);
        }
    }

    @Nested
    @DisplayName("Announce 상단 고정 해제를 테스트한다.")
    class UnFixed {

        @Test
        @DisplayName("Announce 상단 고정 해제에 성공한다.")
        void success1() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            CrewMember CREW_OWNER = crewMemberRepository.getByCrewIdAndMemberId(CREW.getId(), REVI.getId());
            Announce ANNOUNCE = announceJpaRepository.save(FIXED_ANNOUNCE(CREW, CREW_OWNER));

            announceCommandService.fixOrUnfixAnnounce(CREW_OWNER.getId(), CREW.getId(), ANNOUNCE.getId(), false);

            assertThat(events.stream(AnnounceFixedEvent.class)).hasSize(1);
            assertThat(announceRepository.getByIdAndCrewId(ANNOUNCE.getId(), CREW.getId()).isFixed()).isFalse();
            assertThat(announceCacheRepository.fetchCacheByCrewIdAndId(CREW.getId(), ANNOUNCE.getId()).fixed())
                    .isFalse();
        }

        @Test
        @DisplayName("크루 Owner 는 다른 사람이 작성한 Announce 를 상단 고정 해제시킬 수 있다.")
        void success2() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            Member ANDONG = memberRepository.save(ANDONG());
            CrewMember CREW_MANAGER = crewMemberRepository.save(MANAGER_CREW_MEMBER(CREW, ANDONG));
            Announce ANNOUNCE = announceJpaRepository.save(FIXED_ANNOUNCE(CREW, CREW_MANAGER));
            CrewMember CREW_OWNER = crewMemberRepository.getByCrewIdAndMemberId(CREW.getId(), REVI.getId());

            announceCommandService.fixOrUnfixAnnounce(CREW_OWNER.getId(), CREW.getId(), ANNOUNCE.getId(), false);

            assertThat(events.stream(AnnounceFixedEvent.class)).hasSize(1);
            assertThat(announceRepository.getByIdAndCrewId(ANNOUNCE.getId(), CREW.getId()).isFixed()).isFalse();
            assertThat(announceCacheRepository.fetchCacheByCrewIdAndId(CREW.getId(), ANNOUNCE.getId()).fixed())
                    .isFalse();
        }

        @Test
        @DisplayName("이미 Announce 가 상단 고정이 해제되어있으면 무시된다.")
        void success3() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            Member ANDONG = memberRepository.save(ANDONG());
            CrewMember CREW_MANAGER = crewMemberRepository.save(MANAGER_CREW_MEMBER(CREW, ANDONG));
            Announce ANNOUNCE = announceJpaRepository.save(ANNOUNCE(CREW, CREW_MANAGER));
            CrewMember CREW_OWNER = crewMemberRepository.getByCrewIdAndMemberId(CREW.getId(), REVI.getId());

            announceCommandService.fixOrUnfixAnnounce(CREW_OWNER.getId(), CREW.getId(), ANNOUNCE.getId(), false);

            assertThat(events.stream(AnnounceFixedEvent.class)).hasSize(0);
            verify(announceRepository, never()).saveAndFlush(any(Announce.class));
            assertThat(announceRepository.getByIdAndCrewId(ANNOUNCE.getId(), CREW.getId()).isFixed()).isFalse();
        }

        @Test
        @DisplayName("크루 일반 사용자는 Announce 상단 고정 해재에 실패한다.")
        void fail1() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            Member KWANGWON = memberRepository.save(KWANGWON());
            CrewMember CREW_MANAGER = crewMemberRepository.save(MANAGER_CREW_MEMBER(CREW, KWANGWON));
            Announce ANNOUNCE = announceJpaRepository.save(FIXED_ANNOUNCE(CREW, CREW_MANAGER));
            Member ANDONG = memberRepository.save(ANDONG());
            CrewMember CREW_GENERAL = crewMemberRepository.save(GENERAL_CREW_MEMBER(CREW, ANDONG));

            assertThatThrownBy(() -> announceCommandService
                    .fixOrUnfixAnnounce(CREW_GENERAL.getId(), CREW.getId(), ANNOUNCE.getId(), false))
                    .isExactlyInstanceOf(AnnounceBusinessException.CantFix.class);
        }

        @Test
        @DisplayName("크루 매니저는 Announce 상단 고정에 실패한다.")
        void fail2() {
            Member REVI = memberRepository.save(REVI());
            Crew CREW = crewRepository.save(CREW(REVI));
            Member KWANGWON = memberRepository.save(KWANGWON());
            CrewMember CREW_MANAGER = crewMemberRepository.save(MANAGER_CREW_MEMBER(CREW, KWANGWON));
            Announce ANNOUNCE = announceJpaRepository.save(FIXED_ANNOUNCE(CREW, CREW_MANAGER));

            assertThatThrownBy(() -> announceCommandService
                    .fixOrUnfixAnnounce(CREW_MANAGER.getId(), CREW.getId(), ANNOUNCE.getId(), false))
                    .isExactlyInstanceOf(AnnounceBusinessException.CantFix.class);
        }
    }
}
