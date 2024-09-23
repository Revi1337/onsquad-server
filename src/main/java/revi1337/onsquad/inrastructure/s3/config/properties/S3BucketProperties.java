package revi1337.onsquad.inrastructure.s3.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("onsquad.aws")
public record S3BucketProperties(
        @NestedConfigurationProperty S3 s3
) {
    @ConfigurationProperties("s3")
    public record S3(
            String bucket,
            String region,
            String accessKey,
            String secretKey,
            @NestedConfigurationProperty DirectoryProperties directory
    ) {}

    @ConfigurationProperties("directory")
    public record DirectoryProperties(
            String root,
            @NestedConfigurationProperty Directories directories
    ) {
    }

    @ConfigurationProperties("directories")
    public record Directories(
            String crewDirectory,
            String squadDirectory,
            String memberDirectory
    ) {
    }
}
