package revi1337.onsquad.crew.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import revi1337.onsquad.crew.application.initializer.LocalCrewTopMemberInitializer;
import revi1337.onsquad.crew.application.initializer.NonLocalCrewTopMemberInitializer;
import revi1337.onsquad.crew.domain.repository.top.CrewTopMemberRepository;

@Configuration
class CrewTopMemberInitializerSelector {

    @Bean
    @Profile({"local", "default"})
    @ConditionalOnProperty(value = "spring.sql.init.mode", havingValue = "always")
    public LocalCrewTopMemberInitializer localCrewTopMemberInitializer(
            CrewTopMemberRepository crewTopMemberRepository,
            CrewTopMemberProperties crewTopMemberProperties
    ) {
        return new LocalCrewTopMemberInitializer(crewTopMemberRepository, crewTopMemberProperties);
    }

    @Bean
    @Profile({"dev", "prod"})
    public NonLocalCrewTopMemberInitializer nonLocalCrewTopMemberInitializer(
            CrewTopMemberRepository crewTopMemberRepository,
            CrewTopMemberProperties crewTopMemberProperties
    ) {
        return new NonLocalCrewTopMemberInitializer(crewTopMemberRepository, crewTopMemberProperties);
    }
}
