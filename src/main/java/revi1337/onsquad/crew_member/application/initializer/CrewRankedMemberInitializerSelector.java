package revi1337.onsquad.crew_member.application.initializer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import revi1337.onsquad.crew_member.config.CrewRankedMemberProperties;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankedMemberRepository;

@Configuration
public class CrewRankedMemberInitializerSelector {

    @Bean
    @Profile({"local", "default"})
    public CrewRankedMemberInitializer crewRankedMemberInitializer(StringRedisTemplate stringRedisTemplate) {
        return new CrewRankedMemberInitializer(stringRedisTemplate);
    }

    @Bean
    @Profile({"local", "default"})
    @ConditionalOnProperty(value = "spring.sql.init.mode", havingValue = "always")
    public LocalCrewRankedMemberInitializer localCrewRankedMemberInitializer(
            CrewRankedMemberRepository crewRankedMemberRepository,
            CrewRankedMemberProperties crewRankedMemberProperties
    ) {
        return new LocalCrewRankedMemberInitializer(crewRankedMemberRepository, crewRankedMemberProperties);
    }

    @Bean
    @Profile({"dev", "prod"})
    public NonLocalCrewRankedMemberInitializer nonLocalCrewRankedMemberInitializer(
            CrewRankedMemberRepository crewRankedMemberRepository,
            CrewRankedMemberProperties crewRankedMemberProperties
    ) {
        return new NonLocalCrewRankedMemberInitializer(crewRankedMemberRepository, crewRankedMemberProperties);
    }
}
