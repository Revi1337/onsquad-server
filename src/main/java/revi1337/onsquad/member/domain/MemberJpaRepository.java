package revi1337.onsquad.member.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    boolean existsByNickname(Nickname nickname);

    boolean existsByEmail(Email email);

    Optional<Member> findByEmail(Email email);

    List<Member> findAllByEmail(Email email);

}
