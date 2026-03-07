package revi1337.onsquad.infrastructure.aws.s3.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.common.config.etc.YamlPropertySourceFactory;
import revi1337.onsquad.common.config.infrastructure.AwsConfiguration;
import revi1337.onsquad.common.container.AwsTestContainerInitializer;
import revi1337.onsquad.infrastructure.aws.s3.client.S3StorageCleaner.DeletedResult;
import revi1337.onsquad.infrastructure.aws.s3.config.S3BucketProperties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Error;

@TestPropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
@EnableConfigurationProperties(S3BucketProperties.class)
@ContextConfiguration(initializers = AwsTestContainerInitializer.class, classes = AwsConfiguration.S3Configuration.class)
@ExtendWith(SpringExtension.class)
class S3StorageCleanerTest {

    private final S3Client s3Client;
    private final S3BucketProperties s3BucketProperties;
    private final S3StorageCleaner s3StorageCleaner;

    @Autowired
    public S3StorageCleanerTest(S3Client s3Client, S3BucketProperties s3BucketProperties) {
        this.s3Client = spy(s3Client);
        this.s3BucketProperties = s3BucketProperties;
        this.s3StorageCleaner = new S3StorageCleaner(this.s3Client, s3BucketProperties.bucket());
    }

    @BeforeEach
    void setUp() {
        AwsTestContainerInitializer.flushAll();
    }

    @Nested
    class deleteInBatch {

        @Test
        @DisplayName("성공 시 삭제된 객체의 경로 리스트와 빈 실패 리스트를 반환한다.")
        void success() {
            List<String> paths = List.of("member/file-1.txt", "member/file-2.txt");
            paths.forEach(path -> s3Client.putObject(PutObjectRequest.builder()
                    .bucket(s3BucketProperties.bucket())
                    .key(path)
                    .build(), RequestBody.fromString("test-content")));

            DeletedResult deletedResult = s3StorageCleaner.deleteInBatch(paths);

            assertSoftly(softly -> {
                softly.assertThat(deletedResult.deletedPaths()).hasSize(paths.size());
                softly.assertThat(deletedResult.deletedPaths()).containsExactlyInAnyOrderElementsOf(paths);
                softly.assertThat(deletedResult.failedPaths()).isEmpty();
                softly.assertThat(isBucketEmpty("member/")).isTrue();
            });
        }
    }

    @Nested
    class DeleteInBatchQuietlyTest {

        @Test
        @DisplayName("Quiet 모드이므로 성공 시 삭제된 경로 리스트를 비워서 반환한다.")
        void success() {
            List<String> paths = List.of("member/file-1.txt", "member/file-2.txt");
            paths.forEach(path -> s3Client.putObject(PutObjectRequest.builder()
                    .bucket(s3BucketProperties.bucket())
                    .key(path)
                    .build(), RequestBody.fromString("test-content")));
            assertThat(isBucketEmpty("member/")).isFalse();

            DeletedResult deletedResult = s3StorageCleaner.deleteInBatchQuietly(paths);

            assertSoftly(softly -> {
                softly.assertThat(deletedResult.deletedPaths()).isEmpty();
                softly.assertThat(deletedResult.failedPaths()).isEmpty();
                softly.assertThat(isBucketEmpty("member/")).isTrue();
            });
        }

        @Test
        @DisplayName("삭제 실패 시 Quiet 모드와 상관없이 실패한 경로 리스트를 반환한다.")
        void fail() {
            List<String> paths = List.of("member/fail-1.txt", "member/fail-2.txt");
            List<S3Error> mockErrors = paths.stream()
                    .map(path -> S3Error.builder().key(path).code("AccessDenied").message("No Permission").build())
                    .toList();
            DeleteObjectsResponse mockResponse = DeleteObjectsResponse.builder()
                    .errors(mockErrors)
                    .deleted(List.of())
                    .build();
            doReturn(mockResponse).when(s3Client).deleteObjects(any(DeleteObjectsRequest.class));

            DeletedResult deletedResult = s3StorageCleaner.deleteInBatchQuietly(paths);

            assertSoftly(softly -> {
                softly.assertThat(deletedResult.deletedPaths()).isEmpty();
                softly.assertThat(deletedResult.failedPaths())
                        .hasSize(paths.size())
                        .containsExactlyInAnyOrderElementsOf(paths);
            });
        }
    }

    private boolean isBucketEmpty(String prefix) {
        ListObjectsV2Response response = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(s3BucketProperties.bucket())
                .prefix(prefix)
                .maxKeys(1)
                .build());

        return !response.hasContents();
    }
}
