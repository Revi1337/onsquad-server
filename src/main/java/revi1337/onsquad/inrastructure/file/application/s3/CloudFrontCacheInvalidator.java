package revi1337.onsquad.inrastructure.file.application.s3;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationRequest;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationResponse;
import software.amazon.awssdk.services.cloudfront.model.Invalidation;
import software.amazon.awssdk.services.cloudfront.model.InvalidationBatch;
import software.amazon.awssdk.services.cloudfront.model.Paths;

@Slf4j
@RequiredArgsConstructor
public class CloudFrontCacheInvalidator {

    private static final String CREATE_INVALIDATION_RESULT_LOG_FORMAT = "CloudFront create invalidation - id : {} , status : {}";

    private final CloudFrontClient cloudFrontClient;
    private final String distributionId;

    public void createInvalidation(String... paths) {
        InvalidationBatch invalidationBatch = createInvalidationBatch(paths);
        CreateInvalidationRequest request = createInvalidationRequest(invalidationBatch);
        CreateInvalidationResponse response = cloudFrontClient.createInvalidation(request);

        logInvalidationResponse(response);
    }

    private InvalidationBatch createInvalidationBatch(String[] paths) {
        List<String> items = List.of(paths);
        return InvalidationBatch.builder()
                .paths(Paths.builder()
                        .items(items)
                        .quantity(items.size())
                        .build())
                .callerReference(UUID.randomUUID().toString())
                .build();
    }

    private CreateInvalidationRequest createInvalidationRequest(InvalidationBatch invalidationBatch) {
        return CreateInvalidationRequest.builder()
                .distributionId(distributionId)
                .invalidationBatch(invalidationBatch)
                .build();
    }

    private void logInvalidationResponse(CreateInvalidationResponse response) {
        Invalidation invalidation = response.invalidation();
        String status = invalidation.status();

        log.info(CREATE_INVALIDATION_RESULT_LOG_FORMAT, invalidation.id(), status);
    }
}
