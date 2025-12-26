package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import revi1337.onsquad.member.application.dto.MemberCreateDto;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.model.VerificationStatus;
import revi1337.onsquad.member.domain.repository.VerificationCodeRepository;
import revi1337.onsquad.member.error.MemberBusinessException;
import revi1337.onsquad.member.error.MemberErrorCode;

@RequiredArgsConstructor
@Service
public class MemberCreateService {

    private final MemberAccessor memberAccessor;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeRepository redisCodeRepository;

    public Member attemptCreate(MemberCreateDto dto) {
        ensureRequirements(dto);
        Member member = dto.toEntity();
        member.updatePassword(passwordEncoder.encode(dto.password()));
        return member;
    }

    private void ensureRequirements(MemberCreateDto dto) {
        if (!redisCodeRepository.isMarkedVerificationStatusWith(dto.email(), VerificationStatus.SUCCESS)) {
            throw new MemberBusinessException.NonAuthenticateEmail(MemberErrorCode.NON_AUTHENTICATE_EMAIL);
        }
        if (memberAccessor.checkNicknameDuplicate(dto.nickname())) {
            throw new MemberBusinessException.DuplicateNickname(MemberErrorCode.DUPLICATE_NICKNAME);
        }
        if (memberAccessor.checkEmailDuplicate(dto.email())) {
            throw new MemberBusinessException.DuplicateEmail(MemberErrorCode.DUPLICATE_EMAIL);
        }
    }
}
