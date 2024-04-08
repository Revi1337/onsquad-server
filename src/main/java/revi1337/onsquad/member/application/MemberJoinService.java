package revi1337.onsquad.member.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MemberJoinService {

    private final JoinMailService joinMailService;

    public void sendAuthCodeToEmail(String email) {
        String authCode = new RandomCodeGenerator().generate();
        joinMailService.sendAuthCodeToEmail(email, authCode);
    }
}
