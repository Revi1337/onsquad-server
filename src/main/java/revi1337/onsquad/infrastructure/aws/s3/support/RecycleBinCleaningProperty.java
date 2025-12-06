package revi1337.onsquad.infrastructure.aws.s3.support;

import org.springframework.boot.context.properties.ConfigurationProperties;
import revi1337.onsquad.common.config.properties.SchedulingProperty;

@Deprecated
@ConfigurationProperties(prefix = "onsquad.api.clean-recycle-bin")
public record RecycleBinCleaningProperty(
        SchedulingProperty schedule
) {

    public RecycleBinCleaningProperty(SchedulingProperty schedule) {
        this.schedule = schedule;
    }
}
