package revi1337.onsquad.inrastructure.mail.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.inrastructure.mail.application.AuthMailService;
import revi1337.onsquad.inrastructure.mail.presentation.dto.response.EmailValidResponse;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class AuthMailController {

    private final AuthMailService authMailService;

    @PostMapping("/auth/send/email/{email}")
    public ResponseEntity<RestResponse<String>> sendAuthCodeToEmail(
            @PathVariable String email
    ) {
        authMailService.sendAuthCodeToEmail(email);

        return ResponseEntity.ok().body(RestResponse.noContent());
    }

    @GetMapping("/auth/verify/email/{email}")
    public ResponseEntity<RestResponse<EmailValidResponse>> verifyAuthCode(
            @PathVariable String email,
            @RequestParam String code
    ) {
        if (authMailService.verifyAuthCode(email, code)) {
            return ResponseEntity.ok(RestResponse.success(EmailValidResponse.of(true)));
        }

        return ResponseEntity.ok(RestResponse.success(EmailValidResponse.of(false)));
    }
}
