package revi1337.onsquad.token.domain.model;

public record AccessToken(
        String value
) {

    public static AccessToken of(String value) {
        return new AccessToken(value);
    }
}
