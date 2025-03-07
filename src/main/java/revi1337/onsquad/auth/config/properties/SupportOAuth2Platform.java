package revi1337.onsquad.auth.config.properties;

import revi1337.onsquad.auth.application.oauth2.OAuth2Platform;

public enum SupportOAuth2Platform {

    KAKAO(OAuth2Platform.KAKAO),
    GOOGLE(OAuth2Platform.GOOGLE);

    private final OAuth2Platform platform;

    SupportOAuth2Platform(OAuth2Platform platform) {
        this.platform = platform;
    }

    public static OAuth2Platform getAvailableFromSpecific(String platform) {
        try {
            String ignoreCaseOAuthorizationPlatform = platform.toUpperCase();
            SupportOAuth2Platform supportOAuth2Platform = valueOf(ignoreCaseOAuthorizationPlatform);
            return supportOAuth2Platform.platform;
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("unsupported authorization platform " + platform);
        }
    }
}
