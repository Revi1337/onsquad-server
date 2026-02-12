package revi1337.onsquad.auth.security.core;

import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotNull String email,
        @NotNull String password
) {

    public static LoginRequest of(String email, String password) {
        return new LoginRequest(email, password);
    }
}
