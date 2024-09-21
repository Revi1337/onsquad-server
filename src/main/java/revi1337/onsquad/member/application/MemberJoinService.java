package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.auth.error.exception.AuthJoinException;
import revi1337.onsquad.member.application.dto.MemberJoinDto;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;

import java.time.Duration;

import static revi1337.onsquad.auth.error.AuthErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberJoinService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JoinMailService joinMailService;

    public boolean checkDuplicateNickname(String nickname) {
        return memberRepository.existsByNickname(new Nickname(nickname));
    }

    public void sendAuthCodeToEmail(String email) {
        String authCode = new RandomCodeGenerator().generate();
        joinMailService.sendAuthCodeToEmail(email, authCode);
    }

    public boolean verifyAuthCode(String email, String authCode) {
        Duration minutes = Duration.ofMinutes(5);
        return joinMailService.verifyAuthCode(email, authCode, minutes);
    }

    public void joinMember(MemberJoinDto dto) {
        verifyAttribute(dto);
        Member member = dto.toEntity();
        member.updatePassword(passwordEncoder.encode(dto.password()));
        memberRepository.save(member);
    }

    private void verifyAttribute(MemberJoinDto dto) {
        if (memberRepository.existsByNickname(new Nickname(dto.nickname()))) {
            throw new AuthJoinException.DuplicateNickname(DUPLICATE_NICKNAME, dto.nickname());
        }

        if (!joinMailService.isValidMailStatus(dto.email())) {
            throw new AuthJoinException.NonAuthenticateEmail(NON_AUTHENTICATE_EMAIL);
        }

        if (memberRepository.existsByEmail(new Email(dto.email()))) { // TODO 이메일이 아니라 UserType 도 검색조건에 넣어야함.
            throw new AuthJoinException.DuplicateMember(DUPLICATE_MEMBER);
        }
    }
}
