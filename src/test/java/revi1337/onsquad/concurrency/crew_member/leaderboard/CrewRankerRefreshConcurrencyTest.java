package revi1337.onsquad.concurrency.crew_member.leaderboard;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.aspect.ThrottlingAspect;
import revi1337.onsquad.common.config.ApplicationLayerConfiguration;
import revi1337.onsquad.common.container.MySqlTestContainerInitializer;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardUpdateService;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheAspect;
import revi1337.onsquad.infrastructure.storage.sqlite.FileRecycleBinRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.notification.application.listener.NotificationEventListener;

/**
 * Baseline {@code CrewLeaderboardUpdateService.refreshLeaderboards()}는 4/4-1과 달리 원본 테이블
 * (crew_member/squad/squad_member/squad_comment)을 전혀 지우지 않고, 결과 캐시 테이블(crew_ranker)만
 * 지웠다가 다시 채운다. 이 테스트는 "조회 도중 동시에 새로운 크루 가입이 커밋돼도, 그 원본 데이터가
 * refreshLeaderboards()에 의해 유실되지 않는다"를 진짜 스레드 두 개(ExecutorService + CountDownLatch)로
 * 실증한다 — 4/4-1의 "블라인드 시간범위 DELETE" 레이스가 baseline에는 구조적으로 존재하지 않음을 확인.
 */
@Sql({"/mysql-truncate.sql"})
@Import({ApplicationLayerConfiguration.class})
@ContextConfiguration(initializers = MySqlTestContainerInitializer.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@DisplayName("Baseline 리더보드 갱신 동시성: 조회 중 새로 커밋된 가입 활동이 refreshLeaderboards()에 의해 유실되지 않는다")
class CrewRankerRefreshConcurrencyTest {

    @MockBean
    private NotificationEventListener notificationEventListener;

    @MockBean
    private FileRecycleBinRepository fileRecycleBinRepository;

    @MockBean
    private ThrottlingAspect throttlingAspect;

    @MockBean
    private RedisCacheAspect redisCacheAspect;

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberJpaRepository;

    @Autowired
    private CrewRankerRepository crewRankerRepository;

    @Autowired
    private CrewLeaderboardUpdateService leaderboardUpdateService;

    @Test
    @DisplayName("Thread A가 랭킹을 조회한 직후 Thread B가 새 크루 가입을 커밋해도, crew_member 원본 행은 살아있고 다음 refresh에 정상 반영된다")
    void refreshLeaderboards_neverLosesConcurrentlyJoinedMember() {
        // given: 크루만 있고 집계 대상 활동은 아직 없다
        Member owner = memberJpaRepository.save(createRevi());
        Member joiner = memberJpaRepository.save(createMember(1));
        Crew crew = crewJpaRepository.save(createCrew(owner));
        LocalDateTime activityTime = LocalDateTime.of(2026, 1, 6, 12, 0);
        LocalDateTime from = activityTime.minusDays(1);
        LocalDateTime to = activityTime.plusDays(1);

        // when: Thread A는 먼저 랭킹을 조회하고 신호를 보낸 뒤, Thread B의 가입 커밋을 기다렸다가
        //       그제서야 refreshLeaderboards()(delete crew_ranker -> 재조회 -> insert)를 수행한다.
        //       Thread B는 Thread A의 첫 조회가 끝난 뒤에야 새로운 crew_member 행을 커밋한다.
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch firstQueried = new CountDownLatch(1);
        CountDownLatch joinCommitted = new CountDownLatch(1);
        List<CrewRankerCandidate>[] firstPassHolder = new List[1];

        CompletableFuture<Void> threadA = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            firstPassHolder[0] = crewRankerRepository.fetchAggregatedRankedMembers(from, to, 10);
            firstQueried.countDown();

            waitToStart(joinCommitted);
            leaderboardUpdateService.refreshLeaderboards(from, to, 10);
        }, executor);

        CompletableFuture<Void> threadB = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            waitToStart(firstQueried);
            crewMemberJpaRepository.save(CrewMemberFactory.general(crew, joiner, activityTime));
            joinCommitted.countDown();
        }, executor);

        startLatch.countDown();
        CompletableFuture.allOf(threadA, threadB).join();
        executor.shutdown();

        // then
        assertSoftly(softly -> {
            softly.assertThat(firstPassHolder[0])
                    .as("Thread B가 커밋하기 전에 끝난 첫 조회에는 당연히 새 가입이 안 보인다(버그 아님, 정상 스냅샷 격리)")
                    .isEmpty();

            softly.assertThat(crewMemberJpaRepository.findAll())
                    .as("baseline은 crew_member 원본을 지우지 않으므로, 동시에 커밋된 가입 행이 유실되지 않는다")
                    .anyMatch(cm -> cm.getMember().getId().equals(joiner.getId()));

            List<CrewRanker> rankers = crewRankerRepository.findAllByCrewId(crew.getId());
            softly.assertThat(rankers).extracting(CrewRanker::getMemberId)
                    .as("커밋 이후에 실행된 refreshLeaderboards()의 재집계 결과에는 새 가입이 정상 반영된다")
                    .contains(joiner.getId());
        });
    }

    private void waitToStart(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
