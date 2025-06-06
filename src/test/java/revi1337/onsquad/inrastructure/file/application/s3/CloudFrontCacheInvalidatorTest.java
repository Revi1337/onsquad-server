package revi1337.onsquad.inrastructure.file.application.s3;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationRequest;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationResponse;
import software.amazon.awssdk.services.cloudfront.model.Invalidation;

class CloudFrontCacheInvalidatorTest {

    private final String distributionId = "test-distribution-id";
    private final CloudFrontClient cloudFrontClient = mock(CloudFrontClient.class);
    private final CloudFrontCacheInvalidator cloudFrontCacheInvalidator =
            new CloudFrontCacheInvalidator(cloudFrontClient, distributionId);

    @Test
    @DisplayName("CloudFront 캐시 무효화 요청에 성공한다.")
    void success() {
        String uploadPath = "assets/dir/file-name.png";
        Invalidation invalidation = Invalidation.builder().id("invalidation-id").status("InProgress").build();
        CreateInvalidationResponse response = CreateInvalidationResponse.builder().invalidation(invalidation).build();
        when(cloudFrontClient.createInvalidation(any(CreateInvalidationRequest.class)))
                .thenReturn(response);

        cloudFrontCacheInvalidator.createInvalidation(uploadPath);

        verify(cloudFrontClient).createInvalidation(any(CreateInvalidationRequest.class));
    }
}
