package revi1337.onsquad.infrastructure.aws.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.RequestFixture.DEFAULT_JPG_FILE_NAME;
import static revi1337.onsquad.common.fixture.RequestFixture.DEFAULT_PNG_FILE_NAME;
import static revi1337.onsquad.common.fixture.RequestFixture.JPG_MULTIPART;
import static revi1337.onsquad.common.fixture.RequestFixture.PNG_BYTES;
import static revi1337.onsquad.common.fixture.RequestFixture.PNG_MULTIPART;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockMultipartFile;
import revi1337.onsquad.common.ApplicationLayerWithTestContainerSupport;
import revi1337.onsquad.common.application.file.FileStorageManager;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

class S3StorageManagerTest extends ApplicationLayerWithTestContainerSupport {

    @Autowired
    private S3BucketProperties s3BucketProperties;

    @SpyBean
    private S3Client s3Client;

    @Autowired
    private FileStorageManager crewS3StorageManager;

    @Autowired
    private FileStorageManager memberS3StorageManager;

    @Nested
    @DisplayName("Crew 이미지 업로드를 테스트한다.")
    class CrewS3StorageManager {

        private final String crewAssets = s3BucketProperties.getActualCrewAssets();

        @Test
        @DisplayName("ByteArray 를 이용하여 S3 업로드에 성공한다.")
        void successByteArrayUpload() {
            String uploadUrl = crewS3StorageManager.upload(PNG_BYTES, DEFAULT_PNG_FILE_NAME);

            assertThat(uploadUrl).isNotNull();
            assertThat(uploadUrl).contains(crewAssets);
        }

        @Test
        @DisplayName("Multipart 를 이용하여 S3 업로드에 성공한다.")
        void successMultipartUpload() {
            MockMultipartFile multipartFile = PNG_MULTIPART(DEFAULT_PNG_FILE_NAME);

            String uploadUrl = crewS3StorageManager.upload(multipartFile);

            assertThat(uploadUrl).isNotNull();
            assertThat(uploadUrl).contains(crewAssets);
        }

        @Test
        @DisplayName("Multipart 를 이용하여 기존 이미지를 Overwrite 에 성공한다. (CloudFront CacheInvalidate 동작)")
        void successMultipartUpdate() {
            MockMultipartFile multipartFile = PNG_MULTIPART(DEFAULT_PNG_FILE_NAME);
            String uploadUrl = crewS3StorageManager.upload(multipartFile);
            MockMultipartFile updateMultipartFile = JPG_MULTIPART(DEFAULT_JPG_FILE_NAME);

            String updateUrl = crewS3StorageManager.upload(updateMultipartFile, uploadUrl);

            verify(cloudFrontCacheInvalidator).createInvalidation(any(String[].class));
            assertThat(updateUrl).isEqualTo(uploadUrl);
            assertThat(uploadUrl).contains(crewAssets);
        }

        @Test
        @DisplayName("이미지 Delete 에 성공한다.")
        void successDelete() {
            MockMultipartFile multipartFile = PNG_MULTIPART(DEFAULT_PNG_FILE_NAME);
            String uploadUrl = crewS3StorageManager.upload(multipartFile);

            crewS3StorageManager.delete(uploadUrl);

            verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
        }
    }

    @Nested
    @DisplayName("Member 이미지 업로드를 테스트한다.")
    class MemberS3StorageManager {

        private final String memberAssets = s3BucketProperties.getActualMemberAssets();

        @Test
        @DisplayName("ByteArray 를 이용하여 S3 업로드에 성공한다.")
        void successByteArrayUpload() {
            String uploadUrl = memberS3StorageManager.upload(PNG_BYTES, DEFAULT_PNG_FILE_NAME);

            assertThat(uploadUrl).isNotNull();
            assertThat(uploadUrl).contains(memberAssets);
        }

        @Test
        @DisplayName("Multipart 를 이용하여 S3 업로드에 성공한다.")
        void successMultipartUpload() {
            MockMultipartFile multipartFile = PNG_MULTIPART(DEFAULT_PNG_FILE_NAME);

            String uploadUrl = memberS3StorageManager.upload(multipartFile);

            assertThat(uploadUrl).isNotNull();
            assertThat(uploadUrl).contains(memberAssets);
        }

        @Test
        @DisplayName("Multipart 를 이용하여 기존 이미지를 Overwrite 에 성공한다. (CloudFront CacheInvalidate 동작)")
        void successMultipartUpdate() {
            MockMultipartFile multipartFile = PNG_MULTIPART(DEFAULT_PNG_FILE_NAME);
            String uploadUrl = memberS3StorageManager.upload(multipartFile);
            MockMultipartFile updateMultipartFile = JPG_MULTIPART(DEFAULT_JPG_FILE_NAME);

            String updateUrl = memberS3StorageManager.upload(updateMultipartFile, uploadUrl);

            verify(cloudFrontCacheInvalidator).createInvalidation(any(String[].class));
            assertThat(updateUrl).isEqualTo(uploadUrl);
            assertThat(uploadUrl).contains(memberAssets);
        }

        @Test
        @DisplayName("이미지 Delete 에 성공한다.")
        void successDelete() {
            MockMultipartFile multipartFile = PNG_MULTIPART(DEFAULT_PNG_FILE_NAME);
            String uploadUrl = memberS3StorageManager.upload(multipartFile);

            memberS3StorageManager.delete(uploadUrl);

            verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
        }
    }
}
