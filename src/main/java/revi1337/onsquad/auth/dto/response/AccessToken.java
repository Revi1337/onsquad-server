package revi1337.onsquad.auth.dto.response;

public record AccessToken(
        String value
) {
    public static AccessToken of(String value) {
        return new AccessToken(value);
    }
}
