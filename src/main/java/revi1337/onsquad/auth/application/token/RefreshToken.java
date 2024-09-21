package revi1337.onsquad.auth.application.token;

public record RefreshToken(
        String value
) {
    public static RefreshToken of(String value) {
        return new RefreshToken(value);
    }
}
