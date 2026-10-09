package revi1337.onsquad.concurrency.crew;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createMember;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.aspect.ThrottlingAspect;
import revi1337.onsquad.common.config.ApplicationLayerConfiguration;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.error.CrewBusinessException;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.application.CrewMemberCommandService;
import revi1337.onsquad.crew_member.application.CrewMemberCommandServiceFacade;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardService;
import revi1337.onsquad.crew_member.domain.CrewRole;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
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
@DisplayName("Crew 도메인 동시성 경합: 무작위 실행 및 반복 테스트를 통한 데이터 정합성 검증")
class CrewConcurrencyIntegrityTest {

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

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private CrewRequestJpaRepository crewRequestRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberRepository;

    @Autowired
    private CrewRequestCommandService crewRequestCommandService;

    @Autowired
    private CrewMemberCommandService crewMemberCommandService;

    @Autowired
    private CrewMemberCommandServiceFacade crewMemberCommandServiceFacade;

    @RepeatedTest(20)
    @DisplayName("""
            [비결정적] Accept와 Leave가 진짜 동시에 실행되어도 정합성 보장 [Accept(manager1 -> general) vs Leave(manager2)]
            --> 두 작업 모두 "비관적 락"으로 줄을 서므로, 순서에 상관없이 최종 인원수 3명이 유지되어야 함
            """)
    void concurrencyAcceptWithLeave() {
        // given
        Member owner = memberRepository.save(createMember(1));
        Member manager1 = memberRepository.save(createMember(2));
        Member manager2 = memberRepository.save(createMember(3));
        Member general = memberRepository.save(createMember(4));
        Crew crew = createCrew(owner);
        crew.addCrewMember(createManagerCrewMember(crew, manager1), createManagerCrewMember(crew, manager2));
        Crew savedCrew = crewRepository.save(crew);
        CrewRequest request = crewRequestRepository.save(createCrewRequest(savedCrew, general));

        // when
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicBoolean acceptSuccess = new AtomicBoolean(false);
        AtomicBoolean leaveSuccess = new AtomicBoolean(false);
        CompletableFuture<Void> acceptFuture = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            crewRequestCommandService.acceptRequest(manager1.getId(), crew.getId(), request.getId());
            acceptSuccess.set(true);
        }, executor);
        CompletableFuture<Void> leaveFuture = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            crewMemberCommandService.leaveCrew(manager2.getId(), crew.getId());
            leaveSuccess.set(true);
        }, executor);
        startLatch.countDown();
        CompletableFuture.allOf(acceptFuture, leaveFuture).join();
        executor.shutdown();

        // then
        assertSoftly(softly -> {
            softly.assertThat(acceptSuccess.get() && leaveSuccess.get())
                    .as("(manager1가 general 요청 승인) 과 (manager2 탈퇴) 모두 성공해야 한다. (아니면 모순)")
                    .isTrue();

            softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), general.getId()))
                    .as("general은 수락되었다.")
                    .isPresent();
            softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), manager2.getId()))
                    .as("manager2는 탈퇴되었다.")
                    .isEmpty();

            Crew finalCrew = crewRepository.findById(savedCrew.getId()).get();
            softly.assertThat(finalCrew.getCurrentSize())
                    .as("3명 - 1명(manager2 탈퇴) + 1명(general 수락) = 3명")
                    .isEqualTo(3);
            softly.assertThat(finalCrew.getCurrentSize())
                    .as("currentSize와 실제 멤버 수는 일치한다.")
                    .isEqualTo(entityManager.createQuery("select count(cm.id) from CrewMember cm where cm.crew.id = :crewId", Long.class)
                            .setParameter("crewId", savedCrew.getId())
                            .getSingleResult());
        });
    }

    @RepeatedTest(20)
    @DisplayName("""
            [비결정적] Leave와 KickOut이 같은 멤버를 대상으로 경합 [Leave(manager) vs Kick(owner -> manager)]
            --> 누가 먼저 락을 잡든 결과는 "단 한 명의 삭제"로 수렴해야 함 (인원수 정합성 보장)
            """)
    void concurrencyLeaveWithKickOut() {
        // given
        Member owner = memberRepository.save(createMember(1));
        Member manager = memberRepository.save(createMember(2));
        Member general = memberRepository.save(createMember(3));
        Crew crew = createCrew(owner);
        crew.addCrewMember(createManagerCrewMember(crew, manager), createGeneralCrewMember(crew, general));
        Crew savedCrew = crewRepository.save(crew);

        // when
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicBoolean leaveSuccess = new AtomicBoolean(false);
        AtomicBoolean kickoutSuccess = new AtomicBoolean(false);
        AtomicReference<Exception> leaveException = new AtomicReference<>();
        AtomicReference<Exception> kickoutException = new AtomicReference<>();
        CompletableFuture<Void> leaveFuture = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            try {
                crewMemberCommandService.leaveCrew(manager.getId(), crew.getId());
                leaveSuccess.set(true);
            } catch (Exception e) {
                leaveException.set(e);
            }
        }, executor);
        CompletableFuture<Void> kickoutFuture = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            try {
                crewMemberCommandService.kickOutMember(owner.getId(), crew.getId(), manager.getId());
                kickoutSuccess.set(true);
            } catch (Exception e) {
                kickoutException.set(e);
            }
        }, executor);

        startLatch.countDown();
        CompletableFuture.allOf(leaveFuture, kickoutFuture).join();
        executor.shutdown();

        // then
        assertSoftly(softly -> {
            softly.assertThat(leaveSuccess.get() || kickoutSuccess.get())
                    .as("(manager 탈퇴) 와 (owner가 manager 추방) 중 하나는 성공해야 한다.")
                    .isTrue();
            softly.assertThat(leaveSuccess.get() && kickoutSuccess.get())
                    .as("(manager 탈퇴) 와 (owner가 manager 추방) 둘다 성공하면 논리적 모순.")
                    .isFalse();
            softly.assertThat(!leaveSuccess.get() && !kickoutSuccess.get())
                    .as("(manager 탈퇴) 와 (owner가 manager 추방) 둘다 실패해도 논리적 모순.")
                    .isFalse();
            softly.assertThat(leaveSuccess.get() ? kickoutException.get() : leaveException.get())
                    .as("실패한 쪽은 반드시 예외가 발생했어야 한다.")
                    .isNotNull();

            softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), manager.getId()))
                    .as("어쨋거나 manager는 탈퇴 또는 추방됨 ㅇㅇ")
                    .isEmpty();

            Crew finalCrew = crewRepository.findById(savedCrew.getId()).get();
            softly.assertThat(finalCrew.getCurrentSize())
                    .as("3명 - 1명(manager) = 2명")
                    .isEqualTo(2);
            softly.assertThat(finalCrew.getCurrentSize())
                    .as("currentSize와 실제 멤버 수는 일치한다.")
                    .isEqualTo(entityManager.createQuery("select count(cm.id) from CrewMember cm where cm.crew.id = :crewId", Long.class)
                            .setParameter("crewId", savedCrew.getId())
                            .getSingleResult());
        });
    }

    @RepeatedTest(20)
    @DisplayName("""
            [비결정적] Delegate와 Owner Leave가 동시 발생 [Delegate(owner -> general) vs Leave(기존 owner)]
            --> Delegate는 항상 성공해야 함. Leave의 성패는 Delegate의 커밋 시점보다 먼저 체크했는지에 따라 갈리므로,
                "항상 실패"가 아니라 "성공/실패 각각의 결과가 논리적으로 일관되는지"를 검증함
            """)
    void concurrencyDelegateWithOwnerLeave() {
        // given
        Member owner = memberRepository.save(createMember(1));
        Member manager = memberRepository.save(createMember(2));
        Crew crew = createCrew(owner);
        crew.addCrewMember(createManagerCrewMember(crew, manager));
        Crew savedCrew = crewRepository.save(crew);

        // when
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicBoolean delegateSuccess = new AtomicBoolean(false);
        AtomicBoolean leaveSuccess = new AtomicBoolean(false);
        AtomicReference<Exception> delegateException = new AtomicReference<>();
        AtomicReference<Exception> leaveException = new AtomicReference<>();
        CompletableFuture<Void> delegateFuture = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            try {
                crewMemberCommandServiceFacade.delegateOwner(owner.getId(), crew.getId(), manager.getId());
                delegateSuccess.set(true);
            } catch (Exception e) {
                delegateException.set(e);
            }
        }, executor);
        CompletableFuture<Void> leaveFuture = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            try {
                crewMemberCommandService.leaveCrew(owner.getId(), crew.getId());
                leaveSuccess.set(true);
            } catch (Exception e) {
                leaveException.set(e);
            }
        }, executor);
        startLatch.countDown();
        CompletableFuture.allOf(delegateFuture, leaveFuture).join();
        executor.shutdown();

        // then
        assertSoftly(softly -> {
            softly.assertThat(delegateSuccess.get())
                    .as("delegate는 leave의 성패와 무관하게 항상 성공해야 함")
                    .isTrue();
            softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), manager.getId()).get().getRole())
                    .as("manager는 delegate에 의해 항상 새로운 owner로 임명되어야 함")
                    .isSameAs(CrewRole.OWNER);

            if (leaveSuccess.get()) {
                // Case 1: leave의 체크가 delegate의 커밋보다 나중이라, owner가 이미 general로 강등된 뒤라 탈퇴가 성공한 경우
                softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), owner.getId()))
                        .as("탈퇴에 성공했다면 기존 owner는 더 이상 조회되지 않아야 함")
                        .isEmpty();
                softly.assertThat(crewRepository.findById(savedCrew.getId()).get().getCurrentSize())
                        .as("탈퇴에 성공했다면 2명 중 1명(기존 owner) 탈퇴로 1명이 남아야 함")
                        .isEqualTo(1);
            } else {
                // Case 2: leave의 체크가 delegate의 커밋보다 먼저라, 아직 owner인 상태에서 거절된 경우
                softly.assertThat(leaveException.get())
                        .as("탈퇴 실패 원인은 권한 부족(위임 필요)이어야 함")
                        .isExactlyInstanceOf(CrewBusinessException.InsufficientAuthority.class);
                softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), owner.getId()).get().getRole())
                        .as("탈퇴에 실패했다면 기존 owner는 delegate에 의해 general로 강등된 채 남아있어야 함")
                        .isSameAs(CrewRole.GENERAL);
                softly.assertThat(crewRepository.findById(savedCrew.getId()).get().getCurrentSize())
                        .as("탈퇴에 실패했다면 2명 그대로 유지되어야 함")
                        .isEqualTo(2);
            }
        });
    }

    @RepeatedTest(20)
    @DisplayName("""
            [비결정적] Accept와 KickOut이 서로 다른 대상에 대해 동시 실행 [Accept(manager1 -> general) vs KickOut(owner -> manager2)]
            --> 서로 다른 대상을 건드리므로 순서와 무관하게 둘 다 항상 성공해야 하며, currentSize는 +1/-1이 상쇄되어 그대로여야 함
            """)
    void concurrencyAcceptWithKickOut() {
        // given
        Member owner = memberRepository.save(createMember(1));
        Member manager1 = memberRepository.save(createMember(2));
        Member manager2 = memberRepository.save(createMember(3));
        Member general = memberRepository.save(createMember(4));
        Crew crew = createCrew(owner);
        crew.addCrewMember(createManagerCrewMember(crew, manager1), createManagerCrewMember(crew, manager2));
        Crew savedCrew = crewRepository.save(crew);
        CrewRequest request = crewRequestRepository.save(createCrewRequest(savedCrew, general));

        // when
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicBoolean acceptSuccess = new AtomicBoolean(false);
        AtomicBoolean kickoutSuccess = new AtomicBoolean(false);
        AtomicReference<Exception> acceptException = new AtomicReference<>();
        AtomicReference<Exception> kickoutException = new AtomicReference<>();
        CompletableFuture<Void> acceptFuture = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            try {
                crewRequestCommandService.acceptRequest(manager1.getId(), crew.getId(), request.getId());
                acceptSuccess.set(true);
            } catch (Exception e) {
                acceptException.set(e);
            }
        }, executor);
        CompletableFuture<Void> kickoutFuture = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            try {
                crewMemberCommandService.kickOutMember(owner.getId(), crew.getId(), manager2.getId());
                kickoutSuccess.set(true);
            } catch (Exception e) {
                kickoutException.set(e);
            }
        }, executor);
        startLatch.countDown();
        CompletableFuture.allOf(acceptFuture, kickoutFuture).join();
        executor.shutdown();

        // then
        assertSoftly(softly -> {
            softly.assertThat(acceptSuccess.get() && kickoutSuccess.get())
                    .as("accept와 kickout은 항상 성공해야 함")
                    .isTrue();

            softly.assertThat(crewRepository.findById(savedCrew.getId()).get().getCurrentSize())
                    .as("기존(3) + 순서무관(수락(+1) + 추방(-1)) = 3")
                    .isEqualTo(3);

            softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), general.getId()))
                    .as("general은 manager1에 의해 수락되었으므로 크루에 참여하고있어야 함.")
                    .isPresent();
            softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), manager2.getId()))
                    .as("manager2는 owner에 의해 추방되었으므로 크루에 참여하고 있지 않아야 함.")
                    .isEmpty();
        });
    }

    @RepeatedTest(20)
    @DisplayName("""
            [비결정적] Accept와 DelegateOwner가 서로 다른 대상에 대해 동시 실행 [Accept(manager1 -> general) vs Delegate(owner -> manager2)]
            --> 서로 다른 대상을 건드리므로 순서와 무관하게 둘 다 항상 성공해야 하며, 최종 인원수는 +1(general 수락)만 반영되어야 함
            """)
    void concurrencyAcceptWithDelegate() {
        // given
        Member owner = memberRepository.save(createMember(1));
        Member manager1 = memberRepository.save(createMember(2));
        Member manager2 = memberRepository.save(createMember(3));
        Member general = memberRepository.save(createMember(4));
        Crew crew = createCrew(owner);
        crew.addCrewMember(createManagerCrewMember(crew, manager1), createManagerCrewMember(crew, manager2));
        Crew savedCrew = crewRepository.save(crew);
        CrewRequest request = crewRequestRepository.save(createCrewRequest(savedCrew, general));

        // when
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicBoolean acceptSuccess = new AtomicBoolean(false);
        AtomicBoolean delegateSuccess = new AtomicBoolean(false);
        AtomicReference<Exception> acceptException = new AtomicReference<>();
        AtomicReference<Exception> delegateException = new AtomicReference<>();
        CompletableFuture<Void> acceptFuture = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            try {
                crewRequestCommandService.acceptRequest(manager1.getId(), crew.getId(), request.getId());
                acceptSuccess.set(true);
            } catch (Exception e) {
                acceptException.set(e);
            }
        }, executor);
        CompletableFuture<Void> delegateFuture = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            try {
                crewMemberCommandServiceFacade.delegateOwner(owner.getId(), crew.getId(), manager2.getId());
                delegateSuccess.set(true);
            } catch (Exception e) {
                delegateException.set(e);
            }
        }, executor);
        startLatch.countDown();
        CompletableFuture.allOf(acceptFuture, delegateFuture).join();
        executor.shutdown();

        // then
        assertSoftly(softly -> {
            softly.assertThat(acceptSuccess.get() && delegateSuccess.get())
                    .as("accept와 delegateOwner는 항상 성공해야 함")
                    .isTrue();

            softly.assertThat(crewRepository.findById(savedCrew.getId()).get().getCurrentSize())
                    .as("기존(3) + 수락(+1) = 4")
                    .isEqualTo(4);

            softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), general.getId()))
                    .as("general은 manager1에 의해 수락되었으므로 크루에 참여하고있어야 함.")
                    .isPresent();
            softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), manager2.getId()).get().getRole())
                    .as("manager2는 새롭게 owner로 승격되어있어야 함.")
                    .isSameAs(CrewRole.OWNER);
            softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), owner.getId()).get().getRole())
                    .as("기존 owner는 general로 강등되어있어야 함.")
                    .isSameAs(CrewRole.GENERAL);
        });
    }

    @RepeatedTest(20)
    @DisplayName("""
            [비결정적] KickOut과 DelegateOwner가 같은 대상(manager)을 두고 동시 실행 [KickOut(owner -> manager) vs Delegate(owner -> manager)]
            --> 같은 대상을 두고 경합하므로 정확히 하나의 효과만 반영되어야 하며, 최종 상태는 둘 중 하나로 수렴해야 함
            """)
    void concurrencyKickOutWithDelegate() {
        // given
        Member owner = memberRepository.save(createMember(1));
        Member manager = memberRepository.save(createMember(2));
        Crew crew = createCrew(owner);
        crew.addCrewMember(createManagerCrewMember(crew, manager));
        Crew savedCrew = crewRepository.save(crew);

        // when
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CompletableFuture<Void> kickoutFuture = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            try {
                crewMemberCommandServiceFacade.kickOutMember(owner.getId(), savedCrew.getId(), manager.getId());
            } catch (Exception ignored) {
            }
        }, executor);
        CompletableFuture<Void> delegateFuture = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            try {
                crewMemberCommandServiceFacade.delegateOwner(owner.getId(), savedCrew.getId(), manager.getId());
            } catch (Exception ignored) {
            }
        }, executor);
        startLatch.countDown();
        CompletableFuture.allOf(kickoutFuture, delegateFuture).join();
        executor.shutdown();

        // then
        boolean managerKicked = crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), manager.getId()).isEmpty();
        assertSoftly(softly -> {
            if (managerKicked) {
                softly.assertThat(crewRepository.findById(savedCrew.getId()).get().getCurrentSize())
                        .as("kickout이 반영됐다면 2명 중 1명(manager) 추방되어 1명이 남아야 함")
                        .isEqualTo(1);
            } else {
                softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), manager.getId()).get().getRole())
                        .as("delegate가 반영됐다면 manager는 새로운 owner로 승격되어 있어야 함")
                        .isSameAs(CrewRole.OWNER);
                softly.assertThat(crewMemberRepository.findByCrewIdAndMemberId(savedCrew.getId(), owner.getId()).get().getRole())
                        .as("delegate가 반영됐다면 기존 owner는 general로 강등되어 있어야 함")
                        .isSameAs(CrewRole.GENERAL);
                softly.assertThat(crewRepository.findById(savedCrew.getId()).get().getCurrentSize())
                        .as("kickout이 반영 안 됐으므로 2명 그대로 유지되어야 함")
                        .isEqualTo(2);
            }
        });
    }

    private void waitToStart(CountDownLatch start) {
        try {
            start.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private CrewRequest createCrewRequest(Crew crew, Member andong) {
        return CrewRequest.of(crew, andong, LocalDateTime.now());
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
    }
}
