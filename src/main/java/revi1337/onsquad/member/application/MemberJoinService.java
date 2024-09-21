package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.auth.error.exception.AuthJoinException;
import revi1337.onsquad.member.domain.Member;
import revi1337.onsquad.member.domain.MemberJpaRepository;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.dto.MemberDto;

import java.time.Duration;

import static revi1337.onsquad.auth.error.AuthErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberJoinService {

    private final MemberJpaRepository memberRepository;
    private final JoinMailService joinMailService;
    private final PasswordEncoder passwordEncoder;

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

    public void joinMember(MemberDto memberDto) {
        if (verifyAttribute(memberDto)) {
            Member member = memberDto.toEntity();
            member.updatePassword(passwordEncoder.encode(memberDto.getPassword().getValue()));

            memberRepository.save(member);
        }
    }

    private boolean verifyAttribute(MemberDto memberDto) {
        if (memberRepository.existsByNickname(memberDto.getNickname())) {
            throw new AuthJoinException.DuplicateNickname(DUPLICATE_NICKNAME, memberDto.getNickname().getValue());
        }

        Email email = memberDto.getEmail();
        if (!joinMailService.isValidMailStatus(email.getValue())) {
            throw new AuthJoinException.NonAuthenticateEmail(NON_AUTHENTICATE_EMAIL);
        }

        if (memberRepository.existsByEmail(email)) { // TODO 이메일이 아니라 UserType 도 검색조건에 넣어야함.
            throw new AuthJoinException.DuplicateMember(DUPLICATE_MEMBER);
        }

        return true;
    }
}