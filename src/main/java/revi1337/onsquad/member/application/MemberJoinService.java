package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.member.domain.MemberRepository;
import revi1337.onsquad.member.domain.vo.Nickname;

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
}
