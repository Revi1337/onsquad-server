package revi1337.onsquad.infrastructure.aws.s3.client;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.infrastructure.aws.s3.config.S3BucketProperties;

class S3BucketPropertiesTest {

    @TestPropertySource(properties = {
            "onsquad.aws.s3.endpoint=http://onsquad-s3:4566",
            "onsquad.aws.s3.access-key=aws-access-key",
            "onsquad.aws.s3.secret-key=aws-secret-key",
            "onsquad.aws.s3.bucket=test-onsquad",
            "onsquad.aws.s3.region=eu-west-2",
            "onsquad.aws.s3.directory.root=root",
            "onsquad.aws.s3.directory.directories.crew-directory=dummy1",
            "onsquad.aws.s3.directory.directories.squad-directory=dummy2",
            "onsquad.aws.s3.directory.directories.member-directory=dummy3"
    })
    @EnableConfigurationProperties(S3BucketProperties.class)
    @ExtendWith(SpringExtension.class)
    @Nested
    @DisplayName("모든 S3 설정값이 명시적으로 주어질 때")
    class FullTest {

        @Autowired
        private S3BucketProperties s3BucketProperties;

        @Test
        @DisplayName("YML의 계층 구조에 따라 모든 필드에 프로퍼티 값이 올바르게 바인딩된다.")
        void success() {
            assertSoftly(softly -> {
                softly.assertThat(s3BucketProperties.endpoint()).isEqualTo("http://onsquad-s3:4566");
                softly.assertThat(s3BucketProperties.accessKey()).isEqualTo("aws-access-key");
                softly.assertThat(s3BucketProperties.secretKey()).isEqualTo("aws-secret-key");
                softly.assertThat(s3BucketProperties.bucket()).isEqualTo("test-onsquad");
                softly.assertThat(s3BucketProperties.region()).isEqualTo("eu-west-2");
                softly.assertThat(s3BucketProperties.directory().root()).isEqualTo("root");
                softly.assertThat(s3BucketProperties.directory().directories().crewDirectory()).isEqualTo("dummy1");
                softly.assertThat(s3BucketProperties.directory().directories().squadDirectory()).isEqualTo("dummy2");
                softly.assertThat(s3BucketProperties.directory().directories().memberDirectory()).isEqualTo("dummy3");

                softly.assertThat(s3BucketProperties.getActualCrewAssets()).isEqualTo("root/dummy1");
                softly.assertThat(s3BucketProperties.getActualSquadAssets()).isEqualTo("root/dummy2");
                softly.assertThat(s3BucketProperties.getActualMemberAssets()).isEqualTo("root/dummy3");
            });
        }
    }

    @TestPropertySource(properties = {
            "onsquad.aws.s3.access-key=aws-access-key",
            "onsquad.aws.s3.secret-key=aws-secret-key",
            "onsquad.aws.s3.directory.root=root",
            "onsquad.aws.s3.directory.directories.crew-directory=dummy1",
            "onsquad.aws.s3.directory.directories.squad-directory=dummy2",
            "onsquad.aws.s3.directory.directories.member-directory=dummy3"
    })
    @EnableConfigurationProperties(S3BucketProperties.class)
    @ExtendWith(SpringExtension.class)
    @Nested
    @DisplayName("일부 S3 설정값이 누락되었을 때")
    class DefaultValueTest {

        @Autowired
        private S3BucketProperties s3BucketProperties;

        @Test
        @DisplayName("필수값이 아닌 엔드포인트, 버킷, 리전은 생성자에 정의된 @DefaultValue로 바인딩된다.")
        void success() {
            assertSoftly(softly -> {
                softly.assertThat(s3BucketProperties.endpoint()).isEqualTo("http://localhost:4566");
                softly.assertThat(s3BucketProperties.bucket()).isEqualTo("onsquad");
                softly.assertThat(s3BucketProperties.region()).isEqualTo("ap-northeast-2");

                softly.assertThat(s3BucketProperties.getActualCrewAssets()).isEqualTo("root/dummy1");
                softly.assertThat(s3BucketProperties.getActualSquadAssets()).isEqualTo("root/dummy2");
                softly.assertThat(s3BucketProperties.getActualMemberAssets()).isEqualTo("root/dummy3");
            });
        }
    }
}
