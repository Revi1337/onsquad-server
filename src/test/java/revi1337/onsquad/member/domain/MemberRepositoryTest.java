package revi1337.onsquad.member.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.factory.MemberFactory;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("멤버 리포지토리 테스트")
@DataJpaTest(showSql = false)
class MemberRepositoryTest {

    @Autowired private MemberRepository memberRepository;

    @DisplayName("이미 닉네임 중복이 확인되면 true 를 반환한다.")
    @Test
    public void existsByNickname() {
        // given
        Nickname nickname = new Nickname("nickname");
        Member member = MemberFactory.withNickname(nickname);
        memberRepository.save(member);

        // when
        boolean exists = memberRepository.existsByNickname(nickname);

        // then
        assertThat(exists).isTrue();
    }

    @DisplayName("이미 닉네임 중복이 확인되지 않으면 false 를 반환한다.")
    @Test
    public void existsByNickname2() {
        // given
        Nickname nickname = new Nickname("nickname");

        // when
        boolean exists = memberRepository.existsByNickname(nickname);

        // then
        assertThat(exists).isFalse();
    }

}