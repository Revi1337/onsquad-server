package revi1337.onsquad.concurrency.crew;

import static org.assertj.core.api.Assertions.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createKwangwon;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.util.StopWatch;
import revi1337.onsquad.common.aspect.ThrottlingAspect;
import revi1337.onsquad.common.config.ApplicationLayerConfiguration;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardService;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_request.application.CrewRequestCommandService;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestJpaRepository;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheAspect;
import revi1337.onsquad.infrastructure.storage.sqlite.FileRecycleBinRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.notification.application.listener.NotificationEventListener;

@Disabled("동시성 테스트는 스레드 간 격리 문제로 인해 수동 검증 시에만 단독 실행한다. (CI/CD 에서 문제 발생 가능)")
@Sql({"/h2-truncate.sql"})
@Import({ApplicationLayerConfiguration.class})
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class CrewRequestConcurrencyCommandServiceTest {

    @MockBean
    private NotificationEventListener notificationEventListener;

    @MockBean
    private CrewLeaderboardService crewLeaderboardService;

    @MockBean
    private FileRecycleBinRepository fileRecycleBinRepository;

    @MockBean
    private ThrottlingAspect throttlingAspect;

    @MockBean
    private RedisCacheAspect redisCacheAspect;

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private CrewRequestJpaRepository crewRequestRepository;

    @Autowired
    private CrewRequestCommandService commandService;

    @Test
    @DisplayName("락 없이 두 운영진이 동시에 서로 다른 요청을 수락하면 Lost Update로 currentSize 증가분이 유실된다")
    void acceptWithoutLock_losesUpdate() {
        // given
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Member kwangwon = memberRepository.save(createKwangwon());
        Member dummy = memberRepository.save(createMember(1));
        Crew crew = createCrew(revi);
        crew.addCrewMember(createManagerCrewMember(crew, andong));
        Crew savedCrew = crewRepository.save(crew);
        CrewRequest request1 = crewRequestRepository.save(createCrewRequest(savedCrew, kwangwon));
        CrewRequest request2 = crewRequestRepository.save(createCrewRequest(savedCrew, dummy));

        // when
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            commandService.acceptRequest(revi.getId(), crew.getId(), request1.getId());
        }, executor);
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            commandService.acceptRequest(andong.getId(), crew.getId(), request2.getId());
        }, executor);
        stopWatch(TimeUnit.MILLISECONDS, () -> {
            startLatch.countDown();
            CompletableFuture.allOf(future1, future2).join();
        });
        executor.shutdown();

        // then
        assertThat(crewRequestRepository.count())
                .as("두 수락 요청 모두 예외 없이 끝났다 (가입 처리 자체는 성공)")
                .isZero();
        assertThat(crewRepository.findById(crew.getId()).get().getCurrentSize())
                .as("Lost Update: 기대값 4(owner 1 + manager 1 + 수락 2건) 대신 3으로 귀결 — 한쪽의 increaseSize()가 유실됨")
                .isEqualTo(3);
    }

    private void waitToStart(CountDownLatch start) {
        try {
            start.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private CrewRequest createCrewRequest(Crew crew, Member andong) {
        return CrewRequest.of(crew, andong, LocalDateTime.now());
    }

    public void stopWatch(TimeUnit timeUnit, Runnable task) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            task.run();
        } finally {
            stopWatch.stop();
            double totalTime = stopWatch.getTotalTime(timeUnit);
            System.out.printf("Total Time: %d%s%n", (long) totalTime, getAbbreviation(timeUnit));
        }
    }

    private String getAbbreviation(TimeUnit unit) {
        return switch (unit) {
            case NANOSECONDS -> "ns";
            case MICROSECONDS -> "µs";
            case MILLISECONDS -> "ms";
            case SECONDS -> "s";
            case MINUTES -> "m";
            default -> unit.name().toLowerCase();
        };
    }
}
