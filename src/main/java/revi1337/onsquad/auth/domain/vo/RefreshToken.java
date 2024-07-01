package revi1337.onsquad.auth.dto;

public record RefreshToken(
        String value
) {
    public static RefreshToken of(String value) {
        return new RefreshToken(value);
    }
}
