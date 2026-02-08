package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.common.dto.DuplicateResponse;
import revi1337.onsquad.member.application.dto.response.MemberResponse;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberQueryService {

    private final MemberAccessor memberAccessor;

    public DuplicateResponse checkDuplicateNickname(String nickname) {
        if (memberAccessor.checkNicknameDuplicate(nickname)) {
            return DuplicateResponse.of(true);
        }

        return DuplicateResponse.of(false);
    }

    public DuplicateResponse checkDuplicateEmail(String email) {
        if (memberAccessor.checkEmailDuplicate(email)) {
            return DuplicateResponse.of(true);
        }

        return DuplicateResponse.of(false);
    }

    public MemberResponse findMember(Long memberId) {
        return MemberResponse.from(memberAccessor.getById(memberId));
    }
}
