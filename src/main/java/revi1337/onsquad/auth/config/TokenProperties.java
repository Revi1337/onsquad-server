package revi1337.onsquad.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties("onsquad.token")
public record TokenProperties(
        @NestedConfigurationProperty AccessToken accessToken,
        @NestedConfigurationProperty RefreshToken refreshToken
) {
    public TokenProperties(
            @DefaultValue AccessToken accessToken,
            @DefaultValue RefreshToken refreshToken
    ) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public record AccessToken(
            @NestedConfigurationProperty TokenAttributes tokenAttributes
    ) {
        public AccessToken(
                @DefaultValue TokenAttributes tokenAttributes
        ) {
            this.tokenAttributes = tokenAttributes;
        }
    }

    public record RefreshToken(
            @NestedConfigurationProperty TokenAttributes tokenAttributes
    ) {
        public RefreshToken(
                @DefaultValue TokenAttributes tokenAttributes
        ) {
            this.tokenAttributes = tokenAttributes;
        }
    }

    public record TokenAttributes(
            Duration expired,
            String secretKey
    ) {
        public TokenAttributes(
                @DefaultValue("10s") Duration expired,
                @DefaultValue("secret-key") String secretKey
        ) {
            this.expired = expired;
            this.secretKey = secretKey;
        }
    }
}
