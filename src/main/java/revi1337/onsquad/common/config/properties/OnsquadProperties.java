package revi1337.onsquad.common.config.properties;

import lombok.Getter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ToString
@ConfigurationProperties("onsquad")
public class OnsquadProperties {

    @Value("${spring.application.name:onsquad}")
    private String applicationName;

    private final String frontendBaseUrl;

    private final boolean useCustomRedisAspect;

    private final boolean useRedisCacheManager;

    public OnsquadProperties(String applicationName, String frontendBaseUrl, boolean useCustomRedisAspect,
                             boolean useRedisCacheManager) {
        this.applicationName = applicationName;
        this.frontendBaseUrl = frontendBaseUrl;
        this.useCustomRedisAspect = useCustomRedisAspect;
        this.useRedisCacheManager = useRedisCacheManager;
    }
}
