package revi1337.onsquad.auth.verification.presentation;

import jakarta.validation.constraints.NotEmpty;

public record EmailVerifyRequest(
        @NotEmpty String email,
        @NotEmpty String code
) {

}
