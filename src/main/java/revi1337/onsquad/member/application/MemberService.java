package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.dto.MemberInfoDto;
import revi1337.onsquad.member.error.MemberErrorCode;
import revi1337.onsquad.member.error.exception.MemberBusinessException;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberInfoDto findMember(Long memberId) {
        return memberRepository.findById(memberId)
                .map(MemberInfoDto::from)
                .orElseThrow(() -> new MemberBusinessException.NotFound(MemberErrorCode.NOTFOUND, memberId));
    }
}
