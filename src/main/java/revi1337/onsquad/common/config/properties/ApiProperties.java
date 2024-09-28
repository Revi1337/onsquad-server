package revi1337.onsquad.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties("onsquad.api")
public record ApiProperties(
        CrewTopN crewTopN
) {
    public record CrewTopN(
            Duration cycle,
            Integer nSize
    ) {
        public CrewTopN(
                @DefaultValue("7d") Duration cycle,
                @DefaultValue("4") Integer nSize
        ) {
            this.cycle = cycle;
            this.nSize = nSize;
        }
    }
}
