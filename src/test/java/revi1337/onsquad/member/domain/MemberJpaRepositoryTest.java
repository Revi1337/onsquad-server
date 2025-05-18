package revi1337.onsquad.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_EMAIL_VALUE;
import static revi1337.onsquad.common.fixture.MemberValueFixture.REVI_NICKNAME_VALUE;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import revi1337.onsquad.common.PersistenceLayerTestSupport;
import revi1337.onsquad.common.fixture.MemberFixture;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.error.exception.MemberBusinessException;

@Import(MemberRepositoryImpl.class)
class MemberJpaRepositoryTest extends PersistenceLayerTestSupport {

    @Autowired
    private MemberRepository memberRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    @DisplayName("사용자의 id 로 조회했을 때, 사용자가 존재하는지 확인한다.")
    void findById() {
        Member revi = MemberFixture.REVI();
        memberRepository.save(revi);
        entityManager.flush();
        entityManager.clear();

        Optional<Member> optionalMember = memberRepository.findById(revi.getId());

        assertThat(optionalMember).isPresent();
    }

    @Test
    @DisplayName("사용자의 Email 로 조회했을 때, 사용자가 존재하는지 확인한다.")
    void findByEmail() {
        memberRepository.save(MemberFixture.REVI());

        Optional<Member> optionalMember = memberRepository.findByEmail(new Email(REVI_EMAIL_VALUE));

        assertThat(optionalMember).isPresent();
    }

    @Test
    @DisplayName("사용자의 id 로 조회했을 때, 사용자가 존재하지 않으면 예외를 던진다.")
    void getById() {
        Long dummyMemberId = 1L;

        assertThatThrownBy(() -> memberRepository.getById(dummyMemberId))
                .isExactlyInstanceOf(MemberBusinessException.NotFound.class);
    }

    @Test
    @DisplayName("Email 이 사용되고 있으면 true 를 반환한다.")
    void existsByEmail() {
        memberRepository.save(MemberFixture.REVI());

        boolean exists = memberRepository.existsByEmail(new Email(REVI_EMAIL_VALUE));

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Nickname 이 사용되고 있으면 true 를 반환한다.")
    void existsByNickname() {
        memberRepository.save(MemberFixture.REVI());

        boolean exists = memberRepository.existsByNickname(new Nickname(REVI_NICKNAME_VALUE));

        assertThat(exists).isTrue();
    }
}