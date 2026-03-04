package revi1337.onsquad.auth.token.presentation;

import jakarta.validation.constraints.NotEmpty;

public record ReissueRequest(
        @NotEmpty String refreshToken
) {

}
