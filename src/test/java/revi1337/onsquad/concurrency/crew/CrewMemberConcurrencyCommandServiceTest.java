package revi1337.onsquad.concurrency.crew;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.aspect.ThrottlingAspect;
import revi1337.onsquad.common.config.ApplicationLayerConfiguration;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.application.CrewMemberCommandService;
import revi1337.onsquad.crew_member.application.CrewMemberCommandServiceFacade;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardService;
import revi1337.onsquad.crew_member.domain.CrewRole;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheAspect;
import revi1337.onsquad.infrastructure.storage.sqlite.FileRecycleBinRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.notification.application.listener.NotificationEventListener;

@Disabled("동시성 테스트는 스레드 간 격리 문제로 인해 수동 검증 시에만 단독 실행한다. (CI/CD 에서 문제 발생 가능)")
@Sql({"/h2-truncate.sql"})
@Import({ApplicationLayerConfiguration.class})
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class CrewMemberConcurrencyCommandServiceTest {

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
    private CrewMemberJpaRepository crewMemberRepository;

    @Autowired
    private CrewMemberCommandService commandService;

    @Autowired
    private CrewMemberCommandServiceFacade commandServiceFacade;

    @Nested
    class delegateOwner {

        @Test
        @DisplayName("락 없이 두 후보에게 동시에 방장을 위임하면 중복 OWNER가 발생한다")
        void delegateOwnerWithoutLock_duplicatesOwner() {
            // given
            Member owner = memberRepository.save(createMember(1));
            Member nextOwnerCandidate1 = memberRepository.save(createMember(2));
            Member nextOwnerCandidate2 = memberRepository.save(createMember(3));
            Crew crew = createCrew(owner);
            crew.addCrewMember(createManagerCrewMember(crew, nextOwnerCandidate1), createManagerCrewMember(crew, nextOwnerCandidate2));
            Crew savedCrew = crewRepository.save(crew);

            // when
            ExecutorService executor = Executors.newFixedThreadPool(2);
            CountDownLatch startLatch = new CountDownLatch(1);
            CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
                waitToStart(startLatch);
                commandService.delegateOwner(owner.getId(), crew.getId(), nextOwnerCandidate1.getId());
            }, executor);
            CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
                waitToStart(startLatch);
                commandService.delegateOwner(owner.getId(), crew.getId(), nextOwnerCandidate2.getId());
            }, executor);
            startLatch.countDown();
            CompletableFuture.allOf(future1, future2).join();
            executor.shutdown();

            // then
            assertSoftly(softly -> {
                CrewMember delegatedOwner1 = crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), nextOwnerCandidate1.getId()).get();
                CrewMember delegatedOwner2 = crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), nextOwnerCandidate2.getId()).get();

                softly.assertThat(delegatedOwner1.getRole())
                        .as("락 없이 두 스레드 모두 currentOwner.isOwner()==true를 보고 각자 자기 후보를 OWNER로 승격시켜, 두 후보 모두 OWNER가 된다")
                        .isSameAs(CrewRole.OWNER);
                softly.assertThat(delegatedOwner2.getRole())
                        .as("락 없이 두 스레드 모두 currentOwner.isOwner()==true를 보고 각자 자기 후보를 OWNER로 승격시켜, 두 후보 모두 OWNER가 된다")
                        .isSameAs(CrewRole.OWNER);
            });
        }
    }

    @Nested
    class leaveCrew {

        @Test
        @DisplayName("락 없이 두 멤버가 동시에 탈퇴하면 Lost Update로 currentSize 감소분이 유실된다")
        void leaveCrewWithoutLock_losesUpdate() {
            // given
            Member owner = memberRepository.save(createMember(1));
            Member manager = memberRepository.save(createMember(2));
            Member general = memberRepository.save(createMember(3));
            Crew crew = createCrew(owner);
            crew.addCrewMember(createManagerCrewMember(crew, manager), createManagerCrewMember(crew, general));
            Crew savedCrew = crewRepository.save(crew);

            // when
            ExecutorService executor = Executors.newFixedThreadPool(2);
            CountDownLatch startLatch = new CountDownLatch(1);
            CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
                waitToStart(startLatch);
                commandService.leaveCrew(manager.getId(), savedCrew.getId());
            }, executor);
            CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
                waitToStart(startLatch);
                commandService.leaveCrew(general.getId(), savedCrew.getId());
            }, executor);
            startLatch.countDown();
            CompletableFuture.allOf(future1, future2).join();
            executor.shutdown();

            // then
            assertSoftly(softly -> {
                Crew finalCrew = crewRepository.findById(savedCrew.getId()).get();
                softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), manager.getId()))
                        .as("manager 는 탈퇴했기 때문에 조회되지 않는다.")
                        .isEmpty();
                softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), general.getId()))
                        .as("general 는 탈퇴했기 때문에 조회되지 않는다.")
                        .isEmpty();
                softly.assertThat(finalCrew.getCurrentSize())
                        .as("Lost Update: 기대값 1(3명 - 2명 탈퇴) 대신 2로 귀결 — 한쪽의 decreaseSize()가 유실됨")
                        .isEqualTo(2);
            });
        }
    }

    @Nested
    class kickOutMember {

        @Test
        @DisplayName("[Baseline] 락 없이 두 멤버를 동시에 추방하면 Lost Update로 currentSize 감소분이 유실된다")
        void kickOutMemberWithoutLock_losesUpdate() {
            // given
            Member owner = memberRepository.save(createMember(1));
            Member manager = memberRepository.save(createMember(2));
            Member general = memberRepository.save(createMember(3));
            Crew crew = createCrew(owner);
            crew.addCrewMember(createManagerCrewMember(crew, manager), createManagerCrewMember(crew, general));
            Crew savedCrew = crewRepository.save(crew);

            // when
            ExecutorService executor = Executors.newFixedThreadPool(2);
            CountDownLatch startLatch = new CountDownLatch(1);
            CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
                waitToStart(startLatch);
                commandServiceFacade.kickOutMember(owner.getId(), crew.getId(), manager.getId());
            }, executor);
            CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
                waitToStart(startLatch);
                commandServiceFacade.kickOutMember(owner.getId(), crew.getId(), general.getId());
            }, executor);
            startLatch.countDown();
            CompletableFuture.allOf(future1, future2).join();
            executor.shutdown();

            // then
            assertSoftly(softly -> {
                softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), manager.getId()))
                        .as("manager 는 추방되었기 때문에 조회되지 않는다.")
                        .isEmpty();
                softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), general.getId()))
                        .as("general 는 추방되었기 때문에 조회되지 않는다.")
                        .isEmpty();
                softly.assertThat(crewRepository.findById(savedCrew.getId()).get().getCurrentSize())
                        .as("Lost Update: 기대값 1(3명 - 2명 추방) 대신 2로 귀결 — 한쪽의 decreaseSize()가 유실됨")
                        .isEqualTo(2);
            });
        }
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
}
