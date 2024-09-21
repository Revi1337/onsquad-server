package revi1337.onsquad.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;

public record ReissueRequest(
        @NotEmpty String refreshToken
) {
}
