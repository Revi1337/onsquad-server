package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.vo.Email;
import revi1337.onsquad.member.domain.vo.Nickname;
import revi1337.onsquad.member.dto.MemberDto;
import revi1337.onsquad.member.error.exception.DuplicateNickname;
import revi1337.onsquad.member.error.MemberErrorCode;
import revi1337.onsquad.member.error.exception.UnsatisfiedEmailAuthentication;

import java.time.Duration;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberJoinService {

    private final MemberRepository memberRepository;
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

    public void joinMember(MemberDto memberDto) {
        Email email = memberDto.getEmail();
        if (memberRepository.existsByNickname(memberDto.getNickname())) {
            throw new DuplicateNickname(MemberErrorCode.DUPLICATE_NICKNAME);
        }

        if (!joinMailService.isValidMailStatus(email.getValue())) {
            throw new UnsatisfiedEmailAuthentication(MemberErrorCode.NON_AUTHENTICATE_EMAIL);
        }

        memberRepository.save(memberDto.toEntity());
    }
}