package revi1337.onsquad.member.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByNickname(Nickname nickname);

    boolean existsByEmail(Email email);

}
