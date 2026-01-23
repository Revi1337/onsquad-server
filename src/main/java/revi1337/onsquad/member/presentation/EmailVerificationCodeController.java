package revi1337.onsquad.member.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.verification.VerificationMailService;
import revi1337.onsquad.common.dto.RestResponse;
import revi1337.onsquad.member.application.dto.response.EmailValidResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EmailVerificationCodeController {

    private final VerificationMailService verificationMailService;

    @PostMapping("/auth/send")
    public ResponseEntity<RestResponse<String>> sendVerificationCode(
            @RequestParam String email
    ) {
        verificationMailService.sendVerificationCode(email);

        return ResponseEntity.ok().body(RestResponse.created());
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<RestResponse<EmailValidResponse>> verifyVerificationCode(
            @RequestParam String email,
            @RequestParam String code
    ) {
        boolean valid = verificationMailService.validateVerificationCode(email, code);
        if (valid) {
            return ResponseEntity.ok(RestResponse.success(EmailValidResponse.of(true)));
        }

        return ResponseEntity.ok(RestResponse.success(EmailValidResponse.of(false)));
    }
}
