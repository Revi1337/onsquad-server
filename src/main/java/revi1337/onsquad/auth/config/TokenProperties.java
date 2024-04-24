package revi1337.onsquad.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties("onsquad.token")
public record TokenProperties(
        @NestedConfigurationProperty AccessTokenAttributes accessTokenAttributes,
        @NestedConfigurationProperty RefreshTokenAttributes refreshTokenAttributes
) {
    public TokenProperties(
            @DefaultValue AccessTokenAttributes accessTokenAttributes,
            @DefaultValue RefreshTokenAttributes refreshTokenAttributes
    ) {
        this.accessTokenAttributes = accessTokenAttributes;
        this.refreshTokenAttributes = refreshTokenAttributes;
    }

    public record AccessTokenAttributes(
            @NestedConfigurationProperty TokenAttributes tokenAttributes
    ) {
        public AccessTokenAttributes(
                @DefaultValue TokenAttributes tokenAttributes
        ) {
            this.tokenAttributes = tokenAttributes;
        }
    }

    public record RefreshTokenAttributes(
            @NestedConfigurationProperty TokenAttributes tokenAttributes
    ) {
        public RefreshTokenAttributes(
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
