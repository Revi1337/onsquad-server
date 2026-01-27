package revi1337.onsquad.crew_member.application;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_ADDRESS_DETAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_ADDRESS_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_INTRODUCE_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.DUMMY_KAKAO_LINK;
import static revi1337.onsquad.common.fixture.MemberValueFixture.ENCRYPTED_PASSWORD_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.PROFILE_IMAGE_LINK;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.util.StopWatch;
import revi1337.onsquad.common.aspect.RedisCacheAspect;
import revi1337.onsquad.common.aspect.ThrottlingAspect;
import revi1337.onsquad.common.config.ApplicationLayerConfiguration;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_member.application.leaderboard.CrewLeaderboardService;
import revi1337.onsquad.crew_member.domain.entity.CrewMember;
import revi1337.onsquad.crew_member.domain.entity.CrewMemberFactory;
import revi1337.onsquad.crew_member.domain.entity.vo.CrewRole;
import revi1337.onsquad.crew_member.domain.repository.CrewMemberJpaRepository;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.entity.vo.Address;
import revi1337.onsquad.member.domain.entity.vo.Email;
import revi1337.onsquad.member.domain.entity.vo.Introduce;
import revi1337.onsquad.member.domain.entity.vo.Mbti;
import revi1337.onsquad.member.domain.entity.vo.Nickname;
import revi1337.onsquad.member.domain.entity.vo.Password;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.notification.application.listener.NotificationEventListener;

@Import({ApplicationLayerConfiguration.class})
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class CrewMemberConcurrencyCommandServiceTest {

    @MockBean
    private NotificationEventListener notificationEventListener;

    @MockBean
    private CrewLeaderboardService crewLeaderboardService;

    @MockBean
    protected ThrottlingAspect throttlingAspect;

    @MockBean
    protected RedisCacheAspect redisCacheAspect;

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private CrewMemberJpaRepository crewMemberRepository;

    @Autowired
    private CrewMemberCommandService commandService;

    @Test
    @DisplayName("방장 위임 시 동시성 제어가 없으면, 중복 방장이 발생하고 데이터 불일치가 일어난다")
    void delegateOwner() {
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
            Crew finalCrew = crewRepository.findById(savedCrew.getId()).orElseThrow();
            CrewMember delegatedOwner1 = crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), nextOwnerCandidate1.getId()).get();
            CrewMember delegatedOwner2 = crewMemberRepository.findByCrewIdAndMemberId(crew.getId(), nextOwnerCandidate2.getId()).get();

            softly.assertThat(delegatedOwner1.getRole())
                    .as("동시성으로 인해 Owner 가 두명이 된다.")
                    .isSameAs(CrewRole.OWNER);
            softly.assertThat(delegatedOwner2.getRole())
                    .as("동시성으로 인해 Owner 가 두명이 된다.")
                    .isSameAs(CrewRole.OWNER);
            softly.assertThat(finalCrew.getMember().getId())
                    .as("마지막 update 를 치는 주체가 crew 의 실제 owner 가 된다.")
                    .isIn(nextOwnerCandidate1.getId(), nextOwnerCandidate2.getId());

            System.out.printf("Actual Crew Owner: %d\n", finalCrew.getMember().getId());
            System.out.printf("nextOwnerCandidate1: %d role: %s%n", nextOwnerCandidate1.getId(), delegatedOwner1.getRole());
            System.out.printf("nextOwnerCandidate2: %d role: %s%n", nextOwnerCandidate2.getId(), delegatedOwner2.getRole());
        });
    }

    private void waitToStart(CountDownLatch start) {
        try {
            start.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static Member createMember(int sequence) {
        return Member.builder()
                .email(new Email(String.format("test-%d@email.com", sequence)))
                .nickname(new Nickname("m" + sequence))
                .introduce(new Introduce(DUMMY_INTRODUCE_VALUE))
                .address(new Address(DUMMY_ADDRESS_VALUE, DUMMY_ADDRESS_DETAIL_VALUE))
                .password(Password.encrypted(ENCRYPTED_PASSWORD_VALUE))
                .image(PROFILE_IMAGE_LINK)
                .kakaoLink(DUMMY_KAKAO_LINK)
                .mbti(Mbti.ISFP)
                .build();
    }

    private CrewMember createManagerCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.manager(crew, member, LocalDateTime.now());
    }

    private CrewMember createGeneralCrewMember(Crew crew, Member member) {
        return CrewMemberFactory.general(crew, member, LocalDateTime.now());
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
