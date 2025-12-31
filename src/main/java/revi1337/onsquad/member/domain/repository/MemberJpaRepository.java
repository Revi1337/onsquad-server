package revi1337.onsquad.member.domain.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.entity.vo.Email;
import revi1337.onsquad.member.domain.entity.vo.Nickname;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    boolean existsByNickname(Nickname nickname);

    boolean existsByEmail(Email email);

    Optional<Member> findByEmail(Email email);

    List<Member> findByIdIn(List<Long> memberIds);

}
