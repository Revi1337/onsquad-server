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

    private String frontendBaseUrl;

    public OnsquadProperties(String applicationName, String frontendBaseUrl) {
        this.applicationName = applicationName;
        this.frontendBaseUrl = frontendBaseUrl;
    }
}
