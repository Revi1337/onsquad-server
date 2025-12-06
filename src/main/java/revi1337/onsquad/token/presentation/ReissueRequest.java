package revi1337.onsquad.token.presentation;

import jakarta.validation.constraints.NotEmpty;

public record ReissueRequest(
        @NotEmpty String refreshToken
) {

}
