package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.member.application.dto.MemberInfoDto;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.entity.vo.Email;
import revi1337.onsquad.member.domain.entity.vo.Nickname;
import revi1337.onsquad.member.domain.repository.MemberRepository;
import revi1337.onsquad.member.error.MemberErrorCode;
import revi1337.onsquad.member.error.exception.MemberBusinessException;

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
        Member member = validateMemberExistsAndGet(memberId);
        return MemberInfoDto.from(member);
    }

    private Member validateMemberExistsAndGet(Long memberId) { // TODO 리팩토링 싹다끝내면, 하위 private 메서드 모두 책임 분리 필요.
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberBusinessException.NotFound(MemberErrorCode.NOT_FOUND));
    }
}
