package revi1337.onsquad.backup.crew.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import revi1337.onsquad.backup.crew.application.initializer.LocalCrewTopMemberInitializer;
import revi1337.onsquad.backup.crew.application.initializer.NonLocalCrewTopMemberInitializer;
import revi1337.onsquad.backup.crew.config.property.CrewTopMemberProperty;
import revi1337.onsquad.backup.crew.domain.CrewTopMemberRepository;

@RequiredArgsConstructor
@Configuration
public class CrewTopMemberConfiguration {

    private final CrewTopMemberRepository crewTopMemberRepository;
    private final CrewTopMemberProperty crewTopMemberProperty;

    @ConditionalOnProperty(value = "spring.sql.init.mode", havingValue = "always")
    @Profile({"local", "default"})
    @Bean
    public LocalCrewTopMemberInitializer localCrewTopMemberInitializer() {
        return new LocalCrewTopMemberInitializer(crewTopMemberRepository, crewTopMemberProperty);
    }

    @Profile({"dev", "prod"})
    @Bean
    public NonLocalCrewTopMemberInitializer nonLocalCrewTopMemberInitializer() {
        return new NonLocalCrewTopMemberInitializer(crewTopMemberRepository, crewTopMemberProperty);
    }
}
