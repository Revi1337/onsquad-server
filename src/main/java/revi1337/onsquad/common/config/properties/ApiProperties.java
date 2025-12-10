package revi1337.onsquad.common.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import revi1337.onsquad.crew.config.CrewTopMemberProperties;
import revi1337.onsquad.infrastructure.aws.s3.support.RecycleBinCleaningProperty;

@ConfigurationProperties("onsquad.api")
public record ApiProperties(
        @NestedConfigurationProperty CrewTopMemberProperties crewTopMembers,
        @NestedConfigurationProperty RecycleBinCleaningProperty cleanRecycleBin
) {

}
