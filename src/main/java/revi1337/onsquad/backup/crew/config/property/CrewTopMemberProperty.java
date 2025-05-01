package revi1337.onsquad.backup.crew.config.property;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import revi1337.onsquad.common.config.properties.SchedulingProperty;

@ConfigurationProperties(prefix = "onsquad.api.crew-top-members")
public record CrewTopMemberProperty(
        Duration during,
        Integer rankLimit,
        SchedulingProperty schedule
) {
    public CrewTopMemberProperty(
            @DefaultValue("7d") Duration during,
            @DefaultValue("4") Integer rankLimit,
            SchedulingProperty schedule
    ) {
        this.during = during;
        this.rankLimit = rankLimit;
        this.schedule = schedule;
    }
}
