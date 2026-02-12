package revi1337.onsquad.auth.oauth.config;

import lombok.Getter;
import revi1337.onsquad.auth.oauth.application.OAuth2Platform;

@Getter
public enum SupportOAuth2Platform {

    KAKAO(OAuth2Platform.KAKAO),
    GOOGLE(OAuth2Platform.GOOGLE);

    private final OAuth2Platform platform;

    SupportOAuth2Platform(OAuth2Platform platform) {
        this.platform = platform;
    }

    public static SupportOAuth2Platform from(String platform) {
        try {
            return valueOf(platform.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedOperationException("unsupported authorization platform " + platform);
        }
    }

    public static OAuth2Platform getAvailableFrom(String platform) {
        SupportOAuth2Platform supportOAuth2Platform = from(platform);
        return supportOAuth2Platform.platform;
    }
}
