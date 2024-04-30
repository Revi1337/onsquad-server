package revi1337.onsquad.auth.dto.response;

public record RefreshToken(
        String value
) {
    public static RefreshToken of(String value) {
        return new RefreshToken(value);
    }
}
