package revi1337.onsquad.infrastructure.aws.cloudfront;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationRequest;
import software.amazon.awssdk.services.cloudfront.model.CreateInvalidationResponse;
import software.amazon.awssdk.services.cloudfront.model.Invalidation;
import software.amazon.awssdk.services.cloudfront.model.InvalidationBatch;

@ExtendWith(MockitoExtension.class)
class CloudFrontCacheInvalidatorTest {

    private final String distributionId = "DIST-12345";

    @Mock
    private CloudFrontClient cloudFrontClient;

    private CloudFrontCacheInvalidator invoker;

    @BeforeEach
    void setUp() {
        invoker = new CloudFrontCacheInvalidator(cloudFrontClient, distributionId);
    }

    @Test
    @DisplayName("복수의 경로에 대해 CloudFront 캐시 무효화를 요청한다.")
    void createInvalidation_success() {
        String[] paths = {"/onsquad/member/test1.txt", "/onsquad/member/test2.txt"};
        Invalidation mockInvalidation = Invalidation.builder()
                .id("INVALIDATION-ID")
                .status("InProgress")
                .build();
        CreateInvalidationResponse mockResponse = CreateInvalidationResponse.builder()
                .invalidation(mockInvalidation)
                .build();
        when(cloudFrontClient.createInvalidation(any(CreateInvalidationRequest.class)))
                .thenReturn(mockResponse);

        invoker.createInvalidation(paths);

        assertSoftly(softly -> {
            ArgumentCaptor<CreateInvalidationRequest> captor = ArgumentCaptor.forClass(CreateInvalidationRequest.class);
            verify(cloudFrontClient, times(1)).createInvalidation(captor.capture());
            CreateInvalidationRequest actualRequest = captor.getValue();
            InvalidationBatch batch = actualRequest.invalidationBatch();

            softly.assertThat(actualRequest.distributionId()).isEqualTo(distributionId);
            softly.assertThat(batch.paths().items()).containsExactlyInAnyOrder(paths);
            softly.assertThat(batch.paths().quantity()).isEqualTo(paths.length);
            softly.assertThat(batch.callerReference()).isNotBlank();
        });
    }

    @Test
    @DisplayName("매 요청마다 고유한 CallerReference를 생성하여 중복 요청 방지(Idempotency)를 보장한다.")
    void createInvalidation_uniqueCallerReference() {
        when(cloudFrontClient.createInvalidation(any(CreateInvalidationRequest.class)))
                .thenReturn(CreateInvalidationResponse.builder()
                        .invalidation(Invalidation.builder().id("ID").status("S").build())
                        .build());

        invoker.createInvalidation("/path1");
        invoker.createInvalidation("/path2");

        ArgumentCaptor<CreateInvalidationRequest> captor = ArgumentCaptor.forClass(CreateInvalidationRequest.class);
        verify(cloudFrontClient, times(2)).createInvalidation(captor.capture());

        List<CreateInvalidationRequest> requests = captor.getAllValues();
        String ref1 = requests.get(0).invalidationBatch().callerReference();
        String ref2 = requests.get(1).invalidationBatch().callerReference();

        assertThat(ref1).isNotEqualTo(ref2);
    }

}
