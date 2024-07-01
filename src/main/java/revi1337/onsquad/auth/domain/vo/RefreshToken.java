package revi1337.onsquad.auth.domain.vo;

public record RefreshToken(
        String value
) {
    public static RefreshToken of(String value) {
        return new RefreshToken(value);
    }
}
