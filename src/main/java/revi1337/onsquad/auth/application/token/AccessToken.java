package revi1337.onsquad.auth.application.token;

public record AccessToken(
        String value
) {
    public static AccessToken of(String value) {
        if (value == null) {
            throw new IllegalStateException("Access Token cannot be bull");
        }
        
        return new AccessToken(value);
    }
}
