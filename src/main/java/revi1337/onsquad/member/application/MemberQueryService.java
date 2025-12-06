package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.member.application.dto.MemberInfoDto;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.entity.vo.Email;
import revi1337.onsquad.member.domain.entity.vo.Nickname;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberQueryService {

    private final MemberRepository memberRepository;

    public boolean checkDuplicateNickname(String nickname) {
        return memberRepository.existsByNickname(new Nickname(nickname));
    }

    public boolean checkDuplicateEmail(String email) {
        return memberRepository.existsByEmail(new Email(email));
    }

    public MemberInfoDto findMember(Long memberId) {
        Member member = memberRepository.getById(memberId);

        return MemberInfoDto.from(member);
    }
}
