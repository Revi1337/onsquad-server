package revi1337.onsquad.inrastructure.file.config.s3.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import revi1337.onsquad.common.config.properties.SchedulingProperty;

@ConfigurationProperties(prefix = "onsquad.api.clean-recycle-bin")
public record RecycleBinCleaningProperty(
        SchedulingProperty schedule
) {
    public RecycleBinCleaningProperty(SchedulingProperty schedule) {
        this.schedule = schedule;
    }
}
