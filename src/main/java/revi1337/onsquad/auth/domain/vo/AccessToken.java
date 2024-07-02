package revi1337.onsquad.auth.domain.vo;

public record AccessToken(
        String value
) {
    public static AccessToken of(String value) {
        return new AccessToken(value);
    }
}
