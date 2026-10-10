package revi1337.onsquad.auth.verification.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<RestResponse<Void>> sendVerificationCode(
            @RequestParam String email
    ) {
        verificationMailService.sendVerificationCode(email);

        return RestResponse.created().toResponseEntity();
    }

    @PostMapping("/auth/verify")
    public ResponseEntity<RestResponse<EmailValidResponse>> verifyVerificationCode(
            @Valid @RequestBody EmailVerifyRequest request
    ) {
        if (verificationMailService.validateVerificationCode(request.email(), request.code())) {
            return RestResponse.success(EmailValidResponse.of(true)).toResponseEntity();
        }

        return RestResponse.success(EmailValidResponse.of(false)).toResponseEntity();
    }
}
