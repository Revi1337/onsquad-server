package revi1337.onsquad.infrastructure.aws.s3.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import revi1337.onsquad.common.constant.Sign;

@ConfigurationProperties(prefix = "onsquad.aws.s3")
public record S3BucketProperties(
        String endpoint,
        String bucket,
        String region,
        String accessKey,
        String secretKey,
        S3DirectorHierarchy directory
) {

    public S3BucketProperties(
            @DefaultValue("http://localhost:4566") String endpoint,
            @DefaultValue("onsquad") String bucket,
            @DefaultValue("ap-northeast-2") String region,
            String accessKey,
            String secretKey,
            S3DirectorHierarchy directory
    ) {
        this.endpoint = endpoint;
        this.bucket = bucket;
        this.region = region;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.directory = directory;
    }

    public record S3DirectorHierarchy(String root, Directories directories) {

        public record Directories(String crewDirectory, String squadDirectory, String memberDirectory) {

        }
    }

    public String getRootAssets() {
        return directory.root();
    }

    public String getActualCrewAssets() {
        return String.join(Sign.SLASH, getRootAssets(), directory.directories().crewDirectory());
    }

    public String getActualSquadAssets() {
        return String.join(Sign.SLASH, getRootAssets(), directory.directories().squadDirectory());
    }

    public String getActualMemberAssets() {
        return String.join(Sign.SLASH, getRootAssets(), directory.directories().memberDirectory());
    }
}
