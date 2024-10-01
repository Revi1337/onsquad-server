package revi1337.onsquad.member.domain;

import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.error.exception.MemberBusinessException;

import java.util.List;
import java.util.Optional;

import static revi1337.onsquad.member.error.MemberErrorCode.NOTFOUND;

public interface MemberRepository {

    Member save(Member member);

    List<Member> saveAll(List<Member> members);

    Member saveAndFlush(Member member);

    Optional<Member> findById(Long id);

    Optional<Member> findByEmail(Email email);

    boolean existsByNickname(Nickname nickname);

    boolean existsByEmail(Email email);

    default Member getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new MemberBusinessException.NotFound(NOTFOUND, id));
    }
}
