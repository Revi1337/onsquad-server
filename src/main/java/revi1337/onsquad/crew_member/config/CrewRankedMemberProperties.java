package revi1337.onsquad.crew_member.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;
import revi1337.onsquad.common.config.system.properties.SchedulingProperty;

@ConfigurationProperties(prefix = "onsquad.api.crew-rank-members")
public record CrewRankedMemberProperties(
        Duration during,
        Integer rankLimit,
        @NestedConfigurationProperty SchedulingProperty schedule
) {

    public CrewRankedMemberProperties(
            @DefaultValue("7d") Duration during,
            @DefaultValue("4") Integer rankLimit,
            SchedulingProperty schedule
    ) {
        this.during = during;
        this.rankLimit = rankLimit;
        this.schedule = schedule;
    }
}
