package revi1337.onsquad.config;

import org.springframework.test.context.ActiveProfilesResolver;

public class SpringActiveProfilesResolver implements ActiveProfilesResolver {

    public static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

    @Override
    public String[] resolve(Class<?> testClass) {
        String property = System.getProperty(SPRING_PROFILES_ACTIVE);
        return new String[] {property};
    }
}
