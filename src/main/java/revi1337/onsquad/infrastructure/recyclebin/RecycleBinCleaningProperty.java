package revi1337.onsquad.infrastructure.recyclebin;

import org.springframework.boot.context.properties.ConfigurationProperties;
import revi1337.onsquad.common.config.system.properties.SchedulingProperty;

@Deprecated
@ConfigurationProperties(prefix = "onsquad.api.clean-recycle-bin")
public record RecycleBinCleaningProperty(
        SchedulingProperty schedule
) {

    public RecycleBinCleaningProperty(SchedulingProperty schedule) {
        this.schedule = schedule;
    }
}
