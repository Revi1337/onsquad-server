package revi1337.onsquad.config;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.auth.config.TokenProperties;
import revi1337.onsquad.common.config.properties.OnsquadProperties;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = PropertiesConfiguration.PropertyContextInitializer.class)
public abstract class PropertiesConfiguration {

    @Autowired protected TokenProperties tokenProperties;
    @Autowired protected OnsquadProperties onsquadProperties;

    @Configuration
    @EnableConfigurationProperties({TokenProperties.class, OnsquadProperties.class})
    public static class Config {
    }

    static class PropertyContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        private static final String APPLICATION_NAME = "onsquad";
        private static final String ACCESS_TOKEN_SECRET_KEY = "11111111111111111111111111111111111111111111111111111111111111111111111111111111";
        private static final String ACCESS_TOKEN_EXPIRED = "1s";
        private static final String REFRESH_TOKEN_SECRET_KEY = "22222222222222222222222222222222222222222222222222222222222222222222222222222222";
        private static final String REFRESH_TOKEN_EXPIRED = "2s";

        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            Map<String, String> properties = new HashMap<>();
            properties.put("spring.application.name", APPLICATION_NAME);
            properties.put("onsquad.token.access-token-attributes.token-attributes.secret-key", ACCESS_TOKEN_SECRET_KEY);
            properties.put("onsquad.token.access-token-attributes.token-attributes.expired", ACCESS_TOKEN_EXPIRED);
            properties.put("onsquad.token.refresh-token-attributes.token-attributes.secret-key", REFRESH_TOKEN_SECRET_KEY);
            properties.put("onsquad.token.refresh-token-attributes.token-attributes.expired", REFRESH_TOKEN_EXPIRED);

            TestPropertyValues.of(properties).applyTo(applicationContext);
        }
    }
}
