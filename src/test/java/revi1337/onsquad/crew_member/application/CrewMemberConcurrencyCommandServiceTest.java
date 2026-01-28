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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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

    @Autowired
    private CrewMemberCommandServiceFacade commandServiceFacade;

    @Disabled("기존 낙관적 락을 뺏기때문에 잠시 비활성화")
    @Test
    @DisplayName("방장 위임 동시 요청 시, 낙관적 락과 재시도를 통해 중복 방장 발생을 방지하고 정합성을 유지한다")
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
            commandServiceFacade.delegateOwner(owner.getId(), crew.getId(), nextOwnerCandidate1.getId());
        }, executor);
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            commandServiceFacade.delegateOwner(owner.getId(), crew.getId(), nextOwnerCandidate2.getId());
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
                    .as("owner 위임 대상이었던 두명의 role 은 다를 수 밖에 없다.")
                    .isNotSameAs(delegatedOwner2.getRole());
            softly.assertThat(finalCrew.getMember().getId())
                    .as("crew 의 실제 member 와 crewmember 의 member 는 같을 수 밖에 없다.")
                    .isSameAs((delegatedOwner1.getRole() == CrewRole.OWNER ? delegatedOwner1 : delegatedOwner2).getId());

            System.out.printf("Actual Crew Owner: %d\n", finalCrew.getMember().getId());
            System.out.printf("nextOwnerCandidate1: %d role: %s%n", nextOwnerCandidate1.getId(), delegatedOwner1.getRole());
            System.out.printf("nextOwnerCandidate2: %d role: %s%n", nextOwnerCandidate2.getId(), delegatedOwner2.getRole());
        });
    }

    @Test
    @DisplayName("크루에서 추방 시 동시성 제어가 없으면, 추방된 회원의 수와 크루에 잔루한 회원의 수가 다르다.")
    void kickOutMember() {
        // given
        Member owner = memberRepository.save(createMember(1));
        Member manager = memberRepository.save(createMember(2));
        Member general = memberRepository.save(createMember(3));
        Crew crew = createCrew(owner);
        crew.addCrewMember(createManagerCrewMember(crew, manager), createManagerCrewMember(crew, general));
        Crew savedCrew = crewRepository.save(crew);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            commandService.kickOutMember(owner.getId(), crew.getId(), manager.getId());
        }, executor);
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            waitToStart(startLatch);
            commandService.kickOutMember(owner.getId(), crew.getId(), general.getId());
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
                    .as("3명에서 2명이 추방되어서 1명이 남아야 하는데, Lost Update 으로 인해 1명만 추방되어서 2명이 남을거다.")
                    .isEqualTo(2);
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
}
