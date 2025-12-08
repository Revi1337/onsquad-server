package revi1337.onsquad.crew_request.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static revi1337.onsquad.common.fixture.CrewFixture.CREW;
import static revi1337.onsquad.common.fixture.CrewMemberFixture.MANAGER_CREW_MEMBER;
import static revi1337.onsquad.common.fixture.CrewParticipantFixture.CREW_PARTICIPANT;
import static revi1337.onsquad.common.fixture.MemberFixture.ANDONG;
import static revi1337.onsquad.common.fixture.MemberFixture.DUMMY_USER_1;
import static revi1337.onsquad.common.fixture.MemberFixture.DUMMY_USER_2;
import static revi1337.onsquad.common.fixture.MemberFixture.DUMMY_USER_3;
import static revi1337.onsquad.common.fixture.MemberFixture.DUMMY_USER_4;
import static revi1337.onsquad.common.fixture.MemberFixture.DUMMY_USER_5;
import static revi1337.onsquad.common.fixture.MemberFixture.DUMMY_USER_6;
import static revi1337.onsquad.common.fixture.MemberFixture.REVI;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import revi1337.onsquad.common.aspect.RequestCacheHandlerComposite;
import revi1337.onsquad.common.aspect.ThrottlingAspect;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_request.domain.entity.CrewRequest;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestRepository;
import revi1337.onsquad.infrastructure.aws.s3.support.RecycleBinLifeCycleManager;
import revi1337.onsquad.member.application.initializer.VerificationCacheLifeCycleManager;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

//@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:tcp://localhost/~/onsquad-test;MODE=MySQL")
@MockBean({VerificationCacheLifeCycleManager.class, RecycleBinLifeCycleManager.class, RequestCacheHandlerComposite.class, ThrottlingAspect.class})
@SpringBootTest
class CrewRequestCommandServiceConcurrencyTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Autowired
    private CrewJpaRepository crewJpaRepository;

    @Autowired
    private CrewRequestRepository crewRequestRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CrewRequestCommandService crewRequestCommandService;

    @Test
    void success() throws InterruptedException {
        List<Member> members = createMembers();
        Member owner = members.get(0);
        List<Member> managers = members.subList(1, members.size());
        List<Long> managerIds = managers.stream().map(Member::getId).toList();

        Crew crew = createCrewContext(owner, managers);
        assertThat(7L).isEqualTo(countCrewMembersByQuery(crew)).isEqualTo(getCurrentSize(crew));

        Member requestMember = memberJpaRepository.save(ANDONG());
        CrewRequest participant = crewRequestRepository.save(CREW_PARTICIPANT(crew, requestMember));

        System.out.println("==================== start ====================");
        ExecutorService pool = Executors.newFixedThreadPool(managerIds.size());
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(managerIds.size());
        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());
        for (Long managerId : managerIds) {
            pool.submit(() -> {
                try {
                    startLatch.await();
                    crewRequestCommandService.acceptRequest(managerId, crew.getId(), participant.getId());
                } catch (Throwable t) {
                    errors.add(t);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        doneLatch.await();
        pool.shutdown();

        long currentSize = getCurrentSize(crew);
        long realSize = countCrewMembersByQuery(crew);
        System.out.println("Crew : currentSize = " + currentSize);
        System.out.println("Crew : Real Size = " + realSize);
        System.out.println("errors: " + errors.size());
        assertThat(currentSize).isEqualTo(realSize);
        if (!errors.isEmpty()) {
            errors.forEach(Throwable::printStackTrace);
        }
    }

    private List<Member> createMembers() {
        return memberJpaRepository.saveAll(List.of(
                REVI(), DUMMY_USER_1(), DUMMY_USER_2(), DUMMY_USER_3(), DUMMY_USER_4(), DUMMY_USER_5(), DUMMY_USER_6()
        ));
    }

    private Crew createCrewContext(Member owner, List<Member> managers) {
        Crew CREW = crewJpaRepository.save(CREW(owner));
        managers.forEach(manager -> CREW.addCrewMember(MANAGER_CREW_MEMBER(CREW, manager)));
        return crewJpaRepository.saveAndFlush(CREW);
    }

    private long getCurrentSize(Crew crew) {
        return crewJpaRepository.findById(crew.getId()).get().countMembers();
    }

    private long countCrewMembersByQuery(Crew crew) {
        return entityManager.createQuery("select count(cm.id) from CrewMember cm where cm.crew.id = :id", Long.class)
                .setParameter("id", crew.getId())
                .getSingleResult();
    }
}
