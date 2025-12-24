package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.member.application.dto.response.DuplicateResponse;
import revi1337.onsquad.member.application.dto.response.MemberResponse;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberQueryService {

    private final MemberAccessPolicy memberAccessPolicy;

    public DuplicateResponse checkDuplicateNickname(String nickname) {
        if (memberAccessPolicy.checkNicknameDuplicate(nickname)) {
            return DuplicateResponse.of(true);
        }

        return DuplicateResponse.of(false);
    }

    public DuplicateResponse checkDuplicateEmail(String email) {
        if (memberAccessPolicy.checkEmailDuplicate(email)) {
            return DuplicateResponse.of(true);
        }

        return DuplicateResponse.of(false);
    }

    public MemberResponse findMember(Long memberId) {
        return MemberResponse.from(memberAccessPolicy.ensureMemberExistsAndGet(memberId));
    }
}
