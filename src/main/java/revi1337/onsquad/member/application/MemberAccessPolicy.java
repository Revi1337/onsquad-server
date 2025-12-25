package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.entity.vo.Email;
import revi1337.onsquad.member.domain.entity.vo.Nickname;
import revi1337.onsquad.member.domain.repository.MemberRepository;
import revi1337.onsquad.member.error.MemberBusinessException;
import revi1337.onsquad.member.error.MemberErrorCode;

@RequiredArgsConstructor
@Component
public class MemberAccessPolicy {

    private final MemberRepository memberRepository;

    public Member ensureMemberExistsAndGet(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberBusinessException.NotFound(MemberErrorCode.NOT_FOUND));
    }

    public boolean checkNicknameDuplicate(String nickname) {
        return memberRepository.existsByNickname(new Nickname(nickname));
    }

    public boolean checkEmailDuplicate(String email) {
        return memberRepository.existsByEmail(new Email(email));
    }
}
