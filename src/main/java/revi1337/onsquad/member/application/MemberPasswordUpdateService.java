package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import revi1337.onsquad.member.application.dto.MemberPasswordUpdateDto;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.error.MemberBusinessException;
import revi1337.onsquad.member.error.MemberErrorCode;

@RequiredArgsConstructor
@Service
public class MemberPasswordUpdateService {

    private final MemberAccessPolicy memberAccessPolicy;
    private final PasswordEncoder passwordEncoder;

    public Member attemptUpdate(Long memberId, MemberPasswordUpdateDto dto) {
        Member member = memberAccessPolicy.ensureMemberExistsAndGet(memberId);
        if (!passwordEncoder.matches(dto.currentPassword(), member.getPassword().getValue())) {
            throw new MemberBusinessException.WrongPassword(MemberErrorCode.WRONG_PASSWORD);
        }
        member.updatePassword(passwordEncoder.encode(dto.newPassword()));
        return member;
    }
}
