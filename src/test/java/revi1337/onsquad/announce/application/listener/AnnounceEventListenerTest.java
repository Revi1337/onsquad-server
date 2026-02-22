package revi1337.onsquad.announce.application.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.transaction.TestTransaction;
import revi1337.onsquad.announce.application.AnnounceCacheService;
import revi1337.onsquad.announce.application.AnnounceCommandService;
import revi1337.onsquad.announce.application.dto.AnnounceCreateDto;
import revi1337.onsquad.announce.application.dto.AnnounceUpdateDto;
import revi1337.onsquad.announce.domain.entity.Announce;
import revi1337.onsquad.announce.domain.repository.AnnounceRepository;
import revi1337.onsquad.common.config.ApplicationLayerConfiguration;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@Sql({"/h2-truncate.sql"})
@Import(ApplicationLayerConfiguration.class)
@ContextConfiguration(initializers = {RedisTestContainerInitializer.class})
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class AnnounceEventListenerTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CrewRepository crewRepository;

    @Autowired
    private AnnounceRepository announceRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AnnounceCommandService announceCommandService;

    @Autowired
    private AnnounceCacheService announceCacheService;

    @BeforeEach
    void setUp() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
    }

    @Test
    @DisplayName("공지 생성 시, 기존 리스트 캐시는 무효화되어야 한다")
    void onCreate() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        announceRepository.save(createAnnounce(1, crew, revi));
        announceCacheService.getDefaultAnnounces(crew.getId());
        String announcesCacheKey = generateAnnounceListCacheKey(crew.getId());
        assertThat(stringRedisTemplate.hasKey(announcesCacheKey)).isTrue();

        announceCommandService.newAnnounce(revi.getId(), crew.getId(), new AnnounceCreateDto("새 공지", "내용"));
        triggerCommit();

        assertThat(stringRedisTemplate.hasKey(announcesCacheKey)).isFalse();
    }

    @Test
    @DisplayName("공지 수정 시, 해당 단건 캐시와 리스트 캐시가 모두 삭제되어야 한다")
    void onUpdate() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        Announce announce = announceRepository.save(createAnnounce(1, crew, revi));
        announceCacheService.getAnnounce(crew.getId(), announce.getId());
        announceCacheService.getDefaultAnnounces(crew.getId());
        String announceCacheKey = generateAnnounceCacheKey(crew.getId(), announce.getId());
        String announcesCacheKey = generateAnnounceListCacheKey(crew.getId());
        assertThat(stringRedisTemplate.hasKey(announceCacheKey)).isTrue();
        assertThat(stringRedisTemplate.hasKey(announcesCacheKey)).isTrue();

        announceCommandService.updateAnnounce(revi.getId(), crew.getId(), announce.getId(), new AnnounceUpdateDto("수정", "내용"));
        triggerCommit();

        assertSoftly(softly -> {
            softly.assertThat(stringRedisTemplate.hasKey(announceCacheKey)).isFalse();
            softly.assertThat(stringRedisTemplate.hasKey(announcesCacheKey)).isFalse();
        });
    }

    @Test
    @DisplayName("공지 삭제 시, 캐시에서 해당 데이터들이 제거되어야 한다")
    void onDelete() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        Announce announce = announceRepository.save(createAnnounce(1, crew, revi));
        announceCacheService.getAnnounce(crew.getId(), announce.getId());
        announceCacheService.getDefaultAnnounces(crew.getId());
        String announceCacheKey = generateAnnounceCacheKey(crew.getId(), announce.getId());
        String announcesCacheKey = generateAnnounceListCacheKey(crew.getId());
        assertThat(stringRedisTemplate.hasKey(announceCacheKey)).isTrue();
        assertThat(stringRedisTemplate.hasKey(announcesCacheKey)).isTrue();

        announceCommandService.deleteAnnounce(revi.getId(), crew.getId(), announce.getId());
        triggerCommit();

        assertSoftly(softly -> {
            softly.assertThat(stringRedisTemplate.hasKey(announceCacheKey)).isFalse();
            softly.assertThat(stringRedisTemplate.hasKey(announcesCacheKey)).isFalse();
        });
    }

    @Test
    @DisplayName("고정 상태 변경 시, 순서 변경을 위해 리스트 캐시가 삭제되어야 한다")
    void onPinned() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        Announce announce = announceRepository.save(createAnnounce(1, crew, revi));
        announceCacheService.getAnnounce(crew.getId(), announce.getId());
        announceCacheService.getDefaultAnnounces(crew.getId());
        String announceCacheKey = generateAnnounceCacheKey(crew.getId(), announce.getId());
        String announcesCacheKey = generateAnnounceListCacheKey(crew.getId());
        assertThat(stringRedisTemplate.hasKey(announceCacheKey)).isTrue();
        assertThat(stringRedisTemplate.hasKey(announcesCacheKey)).isTrue();

        announceCommandService.changePinState(revi.getId(), crew.getId(), announce.getId(), true);
        triggerCommit();

        assertSoftly(softly -> {
            softly.assertThat(stringRedisTemplate.hasKey(announceCacheKey)).isFalse();
            softly.assertThat(stringRedisTemplate.hasKey(announcesCacheKey)).isFalse();
        });
    }

    private String generateAnnounceListCacheKey(Long crewId) {
        return String.format("onsquad:crew-announces:crew:%d", crewId);
    }

    private String generateAnnounceCacheKey(Long crewId, Long announceId) {
        return String.format("onsquad:crew-announce:crew:%d:announce:%d", crewId, announceId);
    }

    private void triggerCommit() {
        if (TestTransaction.isActive()) {
            TestTransaction.flagForCommit();
            TestTransaction.end();
        }
    }

    private Announce createAnnounce(int sequence, Crew crew, Member member) {
        return new Announce(
                "테스트 제목" + sequence,
                "테스트 내용" + sequence,
                crew,
                member
        );
    }
}
