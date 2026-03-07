package revi1337.onsquad.infrastructure.aws.s3.client;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

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
import revi1337.onsquad.infrastructure.aws.s3.config.S3BucketProperties;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@TestPropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
@EnableConfigurationProperties(S3BucketProperties.class)
@ContextConfiguration(initializers = AwsTestContainerInitializer.class, classes = AwsConfiguration.S3Configuration.class)
@ExtendWith(SpringExtension.class)
class S3StorageManagerTest {

    private final S3Client s3Client;
    private final S3BucketProperties s3BucketProperties;
    private final S3StorageManager s3StorageManager;

    @Autowired
    public S3StorageManagerTest(S3Client s3Client, S3BucketProperties s3BucketProperties) {
        this.s3Client = s3Client;
        this.s3BucketProperties = s3BucketProperties;
        this.s3StorageManager = new S3StorageManager(s3Client, s3BucketProperties.bucket());
    }

    @BeforeEach
    void setUp() {
        AwsTestContainerInitializer.flushAll();
    }

    @Test
    @DisplayName("byte 배열 형식의 파일을 지정된 경로에 성공적으로 업로드한다.")
    void uploadBytes() {
        byte[] fileBytes = "text".getBytes();
        String filePathAndName = "text-bytes-file.txt";

        String uploadedUrl = s3StorageManager.upload(fileBytes, filePathAndName);

        assertSoftly(softly -> {
            softly.assertThat(uploadedUrl).isNotBlank();
            softly.assertThat(uploadedUrl).endsWith("text-bytes-file.txt");
        });
    }

    @Test
    @DisplayName("MultipartFile의 원본 파일명을 사용하여 S3에 성공적으로 업로드한다.")
    void uploadMultipart1() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-file.txt",
                "text/plain",
                "text".getBytes()
        );

        String uploadedUrl = s3StorageManager.upload(mockFile);

        assertSoftly(softly -> {
            softly.assertThat(uploadedUrl).isNotBlank();
            softly.assertThat(uploadedUrl).endsWith("test-file.txt");
        });
    }

    @Test
    @DisplayName("MultipartFile을 업로드할 때, 사용자 지정 파일명을 우선적으로 적용한다.")
    void uploadMultipart2() {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "mock-name.txt",
                "text/plain",
                "text".getBytes()
        );

        String uploadedUrl = s3StorageManager.upload(mockFile, "custom-name.txt");

        assertSoftly(softly -> {
            softly.assertThat(uploadedUrl).isNotBlank();
            softly.assertThat(uploadedUrl).endsWith("custom-name.txt");
        });
    }

    @Test
    @DisplayName("S3에 존재하는 객체를 키(Key) 값을 이용해 정상적으로 삭제한다.")
    void delete() {
        s3StorageManager.upload(new MockMultipartFile(
                "file",
                "test-file.txt",
                "text/plain",
                "text".getBytes()
        ));

        s3StorageManager.delete("test-file.txt");

        assertSoftly(softly -> softly.assertThat(objectExists("test-file.txt")).isFalse());
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
