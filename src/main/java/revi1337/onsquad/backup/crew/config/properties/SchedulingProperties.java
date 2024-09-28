package revi1337.onsquad.backup.crew.config.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.support.CronExpression;

@ConfigurationProperties("onsquad.schedule")
public record SchedulingProperties(
        RefreshCrewTopN refreshCrewTopN
) {
    @Getter
    public static class RefreshCrewTopN {

        private final CronExpression cronExpression;

        public RefreshCrewTopN(String expression) {
            try {
                this.cronExpression = CronExpression.parse(expression);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Cannot parse cron expression: " + expression, e);
            }
        }
    }
}
