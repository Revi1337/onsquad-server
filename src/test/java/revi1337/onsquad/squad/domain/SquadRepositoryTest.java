package revi1337.onsquad.squad.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.factory.MemberFactory;
import revi1337.onsquad.member.domain.Member;

import static org.assertj.core.api.Assertions.assertThat;


//@Transactional
@SpringBootTest
@Disabled
class SquadRepositoryTest {

    @Autowired TestService testService;
    @Autowired TestRepository testRepository;

    @Test
    public void test() {
        Member member = MemberFactory.defaultMember().build();
        Long memberId = testService.join(member);
        Member findMember = testRepository.findOne(memberId);

        System.out.println(member);
        System.out.println(findMember);
        System.out.println(member == findMember);   // 영속성컨텍스트가 다르면 서로 다른 레퍼를 반환해야하는거 아니야? 아 물론 false 가 나오긴하는데, 두 객체의 값이 같아
        assertThat(member).isNotSameAs(findMember);
    }
}

@Transactional
@Repository
class TestRepository {

    @PersistenceContext private EntityManager entityManager;

    public void persist(Member member) {
        entityManager.persist(member);
    }

    public Member findOne(Long id) {
        return entityManager.find(Member.class, id);
    }
}

@Transactional
@Service
class TestService {

    @Autowired private TestRepository testRepository;

    public Long join(Member member) {
        testRepository.persist(member);
        return member.getId();
    }
}