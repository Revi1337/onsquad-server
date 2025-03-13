package revi1337.onsquad.backup.crew.config.properties;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.support.CronExpression;

@ConfigurationProperties("onsquad.schedule")
public record SchedulingProperties(
        RefreshCrewTopMembers refreshCrewTopMembers
) {
    @Getter
    public static class RefreshCrewTopMembers {

        private final CronExpression cronExpression;

        public RefreshCrewTopMembers(String expression) {
            try {
                this.cronExpression = CronExpression.parse(expression);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Cannot parse cron expression: " + expression, e);
            }
        }
    }
}
