package revi1337.onsquad.auth.dto;

public record AccessToken(
        String value
) {
    public static AccessToken of(String value) {
        return new AccessToken(value);
    }
}
