package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.member.application.dto.MemberInfoDto;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberQueryService {

    private final MemberAccessPolicy memberAccessPolicy;

    public boolean checkDuplicateNickname(String nickname) {
        return memberAccessPolicy.checkNicknameDuplicate(nickname);
    }

    public boolean checkDuplicateEmail(String email) {
        return memberAccessPolicy.checkEmailDuplicate(email);
    }

    public MemberInfoDto findMember(Long memberId) {
        return MemberInfoDto.from(memberAccessPolicy.ensureMemberExistsAndGet(memberId));
    }
}
