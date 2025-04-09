package revi1337.onsquad.common.config.properties;

import lombok.Getter;
import org.springframework.scheduling.support.CronExpression;

@Getter
public class SchedulingProperty {

    private final CronExpression cronExpression;

    public SchedulingProperty(String expression) {
        try {
            this.cronExpression = CronExpression.parse(expression);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cannot parse cron expression: " + expression, e);
        }
    }
}
