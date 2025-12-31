package revi1337.onsquad.common.config.web.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import revi1337.onsquad.crew_member.config.CrewRankedMemberProperties;

@ConfigurationProperties("onsquad.api")
public record ApiProperties(
        @NestedConfigurationProperty CrewRankedMemberProperties crewTopMembers
) {

}
