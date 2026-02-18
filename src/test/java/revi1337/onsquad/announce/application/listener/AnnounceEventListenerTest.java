package revi1337.onsquad.announce.application.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.util.List;
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
import revi1337.onsquad.announce.domain.error.AnnounceBusinessException;
import revi1337.onsquad.announce.domain.model.AnnounceDetail;
import revi1337.onsquad.announce.domain.repository.AnnounceJpaRepository;
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
    private AnnounceJpaRepository announceRepository;

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
    @DisplayName("공지 생성 시, 리스트 캐시가 즉시 갱신되어 최신 공지를 포함해야 한다")
    void onCreate() {
        // given
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));

        // when
        announceCommandService.newAnnounce(revi.getId(), crew.getId(), new AnnounceCreateDto("새로운 공지", "내용"));
        triggerCommit();

        // then
        List<AnnounceDetail> list = announceCacheService.getDefaultAnnounces(crew.getId());
        assertThat(list).hasSize(1);
        assertThat(list.get(0).title().getValue()).isEqualTo("새로운 공지");
    }

    @Test
    @DisplayName("공지 수정 시, 단건 캐시와 리스트 캐시가 모두 최신 정보로 동기화되어야 한다")
    void onUpdate() {
        // given
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        announceCommandService.newAnnounce(revi.getId(), crew.getId(), new AnnounceCreateDto("원래 제목", "내용"));
        Announce savedAnnounce = announceRepository.findAll().get(0);
        Long id = savedAnnounce.getId();
        announceCacheService.getAnnounce(crew.getId(), id);

        // when
        announceCommandService.updateAnnounce(revi.getId(), crew.getId(), id, new AnnounceUpdateDto("수정된 제목", "내용"));
        triggerCommit();

        // then
        AnnounceDetail updatedDetail = announceCacheService.getAnnounce(crew.getId(), id);
        assertThat(updatedDetail.title().getValue()).isEqualTo("수정된 제목");
    }

    @Test
    @DisplayName("공지 삭제 시, 단건 캐시는 제거되고 리스트 캐시는 갱신되어야 한다")
    void onDelete() {
        // given
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        announceCommandService.newAnnounce(revi.getId(), crew.getId(), new AnnounceCreateDto("삭제될 공지", "내용"));
        Announce savedAnnounce = announceRepository.findAll().get(0);
        Long id = savedAnnounce.getId();
        announceCacheService.getAnnounce(crew.getId(), id);

        // when
        announceCommandService.deleteAnnounce(revi.getId(), crew.getId(), id);
        triggerCommit();

        // then
        List<AnnounceDetail> list = announceCacheService.getDefaultAnnounces(crew.getId());
        assertThat(list).isEmpty();
        assertThatThrownBy(() -> announceCacheService.getAnnounce(crew.getId(), id))
                .isInstanceOf(AnnounceBusinessException.NotFound.class);
    }

    @Test
    @DisplayName("공지 고정 상태 변경 시, 캐시가 무효화되어 바뀐 순서가 반영되어야 한다")
    void onPinned() {
        Member revi = memberRepository.save(createRevi());
        Crew crew = crewRepository.save(createCrew(revi));
        announceCommandService.newAnnounce(revi.getId(), crew.getId(), new AnnounceCreateDto("공지", "내용"));
        Announce savedAnnounce = announceRepository.findAll().get(0);
        Long id = savedAnnounce.getId();
        announceCacheService.getAnnounce(crew.getId(), id);

        // when
        announceCommandService.changePinState(revi.getId(), crew.getId(), id, true); // Pin 고정
        triggerCommit();

        // then
        AnnounceDetail pinnedDetail = announceCacheService.getAnnounce(crew.getId(), id);
        assertThat(pinnedDetail.pinned()).isTrue();
    }

    private void triggerCommit() {
        if (TestTransaction.isActive()) {
            TestTransaction.flagForCommit();
            TestTransaction.end();
        }
    }
}
