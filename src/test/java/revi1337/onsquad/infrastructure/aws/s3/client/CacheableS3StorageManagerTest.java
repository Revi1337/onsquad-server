package revi1337.onsquad.infrastructure.aws.s3.client;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.common.config.etc.YamlPropertySourceFactory;
import revi1337.onsquad.common.config.infrastructure.AwsConfiguration;
import revi1337.onsquad.common.container.AwsTestContainerInitializer;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontCacheInvalidator;
import revi1337.onsquad.infrastructure.aws.cloudfront.CloudFrontProperties;
import revi1337.onsquad.infrastructure.aws.s3.config.S3BucketProperties;
import software.amazon.awssdk.services.cloudfront.CloudFrontClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@TestPropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
@EnableConfigurationProperties({S3BucketProperties.class, CloudFrontProperties.class})
@ContextConfiguration(
        initializers = AwsTestContainerInitializer.class,
        classes = {AwsConfiguration.S3Configuration.class, AwsConfiguration.CloudFrontConfiguration.class}
)
@ExtendWith(SpringExtension.class)
class CacheableS3StorageManagerTest {

    private final S3Client s3Client;
    private final S3BucketProperties s3BucketProperties;
    private final S3StorageManager s3StorageManager;
    private final CloudFrontProperties cloudFrontProperties;
    private final CloudFrontCacheInvalidator cloudFrontCacheInvalidator;
    private final CacheableS3StorageManager cacheableS3StorageManager;

    @Autowired
    public CacheableS3StorageManagerTest(
            S3Client s3Client,
            S3BucketProperties s3BucketProperties,
            CloudFrontProperties cloudFrontProperties,
            CloudFrontClient cloudFrontClient
    ) {
        this.s3Client = s3Client;
        this.s3BucketProperties = s3BucketProperties;
        this.s3StorageManager = new S3StorageManager(s3Client, s3BucketProperties.bucket());
        this.cloudFrontProperties = cloudFrontProperties;
        this.cloudFrontCacheInvalidator = mock(CloudFrontCacheInvalidator.class);
        this.cacheableS3StorageManager = new CacheableS3StorageManager(
                this.s3StorageManager,
                this.cloudFrontCacheInvalidator,
                cloudFrontProperties.baseDomain()
        );
    }

    @BeforeEach
    void setUp() {
        AwsTestContainerInitializer.flushAll();
    }

    @Test
    @DisplayName("byte 배열 업로드 시, S3에 파일을 저장하고 CloudFront 도메인이 결합된 URL을 반환한다.")
    void uploadBytes() {
        byte[] fileBytes = "text".getBytes();
        String filePathAndName = "text-bytes-file.txt";

        String uploadedUrl = cacheableS3StorageManager.upload(fileBytes, filePathAndName);

        assertSoftly(softly -> {
            softly.assertThat(uploadedUrl).isNotBlank();
            softly.assertThat(uploadedUrl).startsWith(cloudFrontProperties.baseDomain());
            softly.assertThat(uploadedUrl).endsWith("text-bytes-file.txt");
        });
    }

    @Test
    @DisplayName("MultipartFile 업로드 시, 원본 파일명을 유지하며 CloudFront 기반의 접근 주소를 생성한다.")
    void uploadMultipart1() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-file.txt",
                "text/plain",
                "text".getBytes()
        );

        String uploadedUrl = cacheableS3StorageManager.upload(mockFile);

        assertSoftly(softly -> {
            softly.assertThat(uploadedUrl).isNotBlank();
            softly.assertThat(uploadedUrl).startsWith(cloudFrontProperties.baseDomain());
            softly.assertThat(uploadedUrl).endsWith("test-file.txt");
        });
    }

    @Test
    @DisplayName("커스텀 파일명을 지정하여 업로드할 경우, 해당 이름으로 저장 후 CloudFront URL을 반환한다.")
    void uploadMultipart2() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "mock-name.txt",
                "text/plain",
                "text".getBytes()
        );

        String uploadedUrl = cacheableS3StorageManager.upload(mockFile, "custom-name.txt");

        assertSoftly(softly -> {
            softly.assertThat(uploadedUrl).isNotBlank();
            softly.assertThat(uploadedUrl).startsWith(cloudFrontProperties.baseDomain());
            softly.assertThat(uploadedUrl).endsWith("custom-name.txt");
        });
    }

    @Test
    @DisplayName("파일 삭제 시, S3 객체를 제거하고 CloudFront 캐시 무효화(Invalidation)를 요청한다.")
    void delete() {
        cacheableS3StorageManager.upload(new MockMultipartFile(
                "file",
                "test-file.txt",
                "text/plain",
                "text".getBytes()
        ));
        doNothing().when(cloudFrontCacheInvalidator).createInvalidation(any());

        cacheableS3StorageManager.delete("test-file.txt");

        assertSoftly(softly -> {
            softly.assertThat(objectExists("test-file.txt")).isFalse();
            verify(cloudFrontCacheInvalidator, times(1)).createInvalidation(any());
        });
    }

    private boolean objectExists(String key) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(s3BucketProperties.bucket())
                    .key(key)
                    .build());
            return true;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw e;
        }
    }
}
