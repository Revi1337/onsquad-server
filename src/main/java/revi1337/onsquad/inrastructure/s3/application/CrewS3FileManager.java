package revi1337.onsquad.inrastructure.s3.application;

import org.springframework.stereotype.Component;
import revi1337.onsquad.inrastructure.s3.config.properties.S3BucketProperties;
import software.amazon.awssdk.services.s3.S3Client;

@Component
public class CrewS3FileManager extends DefaultS3FileManager {

    private final S3BucketProperties s3BucketProperties;

    public CrewS3FileManager(S3Client s3Client, S3BucketProperties s3BucketProperties) {
        super(s3Client, s3BucketProperties);
        this.s3BucketProperties = s3BucketProperties;
    }

    @Override
    public String getUploadDir() {
        return s3BucketProperties.s3().directory().directories().crewDirectory();
    }
}
