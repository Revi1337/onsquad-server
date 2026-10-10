package revi1337.onsquad.auth.verification.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import revi1337.onsquad.auth.verification.application.VerificationMailService;
import revi1337.onsquad.auth.verification.application.response.EmailValidResponse;
import revi1337.onsquad.common.dto.RestResponse;

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
        RestResponse<String> response = RestResponse.created();

        return ResponseEntity.status(response.status()).body(response);
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<RestResponse<EmailValidResponse>> verifyVerificationCode(
            @RequestParam String email,
            @RequestParam String code
    ) {
        if (verificationMailService.validateVerificationCode(email, code)) {
            RestResponse<EmailValidResponse> response = RestResponse.success(EmailValidResponse.of(true));
            return ResponseEntity.status(response.status()).body(response);
        }

        RestResponse<EmailValidResponse> response = RestResponse.success(EmailValidResponse.of(false));
        return ResponseEntity.status(response.status()).body(response);
    }
}
