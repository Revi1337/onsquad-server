package revi1337.onsquad.common.config.properties;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties("onsquad.api")
public record ApiProperties(
        CrewTopMembers crewTopMembers
) {
    public record CrewTopMembers(
            Duration during,
            Integer rankLimit
    ) {
        public CrewTopMembers(
                @DefaultValue("7d") Duration during,
                @DefaultValue("4") Integer rankLimit
        ) {
            this.during = during;
            this.rankLimit = rankLimit;
        }
    }
}
