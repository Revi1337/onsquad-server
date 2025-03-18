package revi1337.onsquad.inrastructure.mail.application;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import revi1337.onsquad.member.application.RandomCodeGenerator;

@RequiredArgsConstructor
@Service
public class AuthMailService {

    private final AuthMailManager authMailManager;
    private final RandomCodeGenerator codeGenerator;

    public void sendAuthCodeToEmail(String email) {
        String authCode = codeGenerator.generate();
        authMailManager.sendAuthCodeToEmail(email, authCode, Duration.ofMinutes(3));
    }

    public boolean verifyAuthCode(String email, String authCode) {
        Duration minutes = Duration.ofMinutes(5);
        return authMailManager.verifyAuthCode(email, authCode, minutes);
    }
}
