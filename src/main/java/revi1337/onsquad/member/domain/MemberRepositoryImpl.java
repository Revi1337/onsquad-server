package revi1337.onsquad.member.domain;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;

@RequiredArgsConstructor
@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(member);
    }

    @Override
    public List<Member> saveAll(List<Member> members) {
        return memberJpaRepository.saveAll(members);
    }

    @Override
    public Member saveAndFlush(Member member) {
        return memberJpaRepository.saveAndFlush(member);
    }

    @Override
    public Member getReferenceById(Long id) {
        return memberJpaRepository.getReferenceById(id);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberJpaRepository.findById(id);
    }

    @Override
    public Optional<Member> findByEmail(Email email) {
        return memberJpaRepository.findByEmail(email);
    }

    @Override
    public boolean existsByNickname(Nickname nickname) {
        return memberJpaRepository.existsByNickname(nickname);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return memberJpaRepository.existsByEmail(email);
    }
}
