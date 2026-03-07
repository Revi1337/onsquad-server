package revi1337.onsquad.infrastructure.aws.s3.cleanup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.zaxxer.hikari.HikariDataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.IntStream;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import revi1337.onsquad.common.config.etc.YamlPropertySourceFactory;
import revi1337.onsquad.common.config.infrastructure.AwsConfiguration;
import revi1337.onsquad.common.container.AwsTestContainerInitializer;
import revi1337.onsquad.infrastructure.aws.s3.cleanup.model.FilePath;
import revi1337.onsquad.infrastructure.aws.s3.cleanup.model.FilePaths;
import revi1337.onsquad.infrastructure.aws.s3.client.S3StorageCleaner;
import revi1337.onsquad.infrastructure.aws.s3.config.S3BucketProperties;
import revi1337.onsquad.infrastructure.aws.s3.config.S3ThreadPoolConfig;
import revi1337.onsquad.infrastructure.storage.sqlite.ImageRecycleBinRepository;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

@TestPropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
@EnableConfigurationProperties(S3BucketProperties.class)
@ContextConfiguration(
        initializers = AwsTestContainerInitializer.class,
        classes = {AwsConfiguration.S3Configuration.class, S3ThreadPoolConfig.class, S3ImageCleanupProcessorTest.SqliteDataSourcePropertiesConfig.class}
)
@ExtendWith(SpringExtension.class)
@DisplayName("S3 이미지 정리 프로세서 테스트")
class S3ImageCleanupProcessorTest {

    private final DataSource dataSource;
    private final S3Client s3Client;
    private final S3BucketProperties s3BucketProperties;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ImageRecycleBinRepository imageRecyclebinRepository;
    private final S3ImageCleanupProcessor s3ImageCleanupProcessor;

    @Autowired
    public S3ImageCleanupProcessorTest(
            @Qualifier("s3DeletionExecutor") Executor s3DeletionExecutor,
            @Qualifier("sqliteDataSource") DataSource dataSource,
            S3Client s3Client,
            S3BucketProperties s3BucketProperties
    ) {
        this.dataSource = dataSource;
        this.s3Client = s3Client;
        this.s3BucketProperties = s3BucketProperties;
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.imageRecyclebinRepository = new ImageRecycleBinRepository(dataSource);
        this.s3ImageCleanupProcessor = new S3ImageCleanupProcessor(
                s3DeletionExecutor,
                this.imageRecyclebinRepository,
                new S3StorageCleaner(this.s3Client, s3BucketProperties.bucket())
        );
    }

    @BeforeEach
    void setUp() {
        AwsTestContainerInitializer.flushAll();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("DROP TABLE IF EXISTS image_recycle_bin");
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS image_recycle_bin (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    path TEXT NOT NULL,
                    retry_count INTEGER NOT NULL DEFAULT 0
                )
                """);
    }

    @Test
    @DisplayName("휴지통에 저장된 모든 삭제 대상 파일을 조회한다")
    void findAllTargets() {
        int totalCount = 1000;
        List<String> paths = IntStream.rangeClosed(1, totalCount)
                .mapToObj(seq -> String.format("member/file-%d.txt", seq))
                .toList();
        imageRecyclebinRepository.insertBatch(paths);

        FilePaths filePaths = s3ImageCleanupProcessor.findAllTargets();

        assertThat(filePaths.size()).isEqualTo(totalCount);
    }

    @Test
    @DisplayName("S3 저장소에서 대상 파일들을 실제로 삭제 처리한다")
    void executeS3Deletion() {
        int totalCount = 5000;
        List<String> paths = IntStream.rangeClosed(1, totalCount)
                .mapToObj(seq -> String.format("member/file-%d.txt", seq))
                .toList();
        imageRecyclebinRepository.insertBatch(paths);
        FilePaths filePaths = s3ImageCleanupProcessor.findAllTargets();

        S3ImageCleanupProcessor.CleanupResult cleanupResult = s3ImageCleanupProcessor.executeS3Deletion(filePaths);

        assertSoftly(softly -> {
            softly.assertThat(cleanupResult.success().size()).isEqualTo(totalCount);
            softly.assertThat(cleanupResult.failure().size()).isZero();
            softly.assertThat(isBucketEmpty("member/")).isTrue();
        });
    }

    @Test
    @DisplayName("삭제 실패 건의 재시도 횟수를 증가시키고 임계치 도달 대상을 추출한다")
    void updateRetryCountAndGetExceeded() {
        int totalCount = 1000;
        List<String> success = new ArrayList<>();
        List<String> fail = new ArrayList<>();
        IntStream.rangeClosed(1, totalCount).forEach(seq -> {
            String formatted = String.format("member/file-%d.txt", seq);
            if (seq % 2 != 0) {
                success.add(formatted);
            } else {
                fail.add(formatted);
            }
        });
        insertTargetsWithRetryCount(success, 1);
        insertTargetsWithRetryCount(fail, 4);
        FilePaths failedPaths = s3ImageCleanupProcessor.findAllTargets().filterByPaths(fail);

        FilePaths result = s3ImageCleanupProcessor.updateRetryCountAndGetExceeded(failedPaths);

        assertSoftly(softly -> {
            softly.assertThat(result.pathValues())
                    .hasSize(fail.size())
                    .containsExactlyInAnyOrderElementsOf(fail);

            softly.assertThat(s3ImageCleanupProcessor.findAllTargets().filterByPaths(fail).values())
                    .extracting(FilePath::getRetryCount)
                    .allMatch(count -> count == 5);

            softly.assertThat(s3ImageCleanupProcessor.findAllTargets().filterByPaths(success).values())
                    .extracting(FilePath::getRetryCount)
                    .allMatch(count -> count == 1);
        });
    }

    @Test
    @DisplayName("정리가 완료된 데이터를 휴지통 DB에서 영구 삭제한다")
    void deleteFromRecycleBin() {
        int totalCount = 1000;
        List<String> paths = IntStream.rangeClosed(1, totalCount)
                .mapToObj(seq -> String.format("member/file-%d.txt", seq))
                .toList();
        imageRecyclebinRepository.insertBatch(paths);
        FilePaths filePaths = s3ImageCleanupProcessor.findAllTargets();

        s3ImageCleanupProcessor.deleteFromRecycleBin(filePaths);

        assertThat(s3ImageCleanupProcessor.findAllTargets().size()).isZero();
    }

    private boolean isBucketEmpty(String prefix) {
        ListObjectsV2Response response = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(s3BucketProperties.bucket())
                .prefix(prefix)
                .maxKeys(1)
                .build());

        return !response.hasContents();
    }

    private void insertTargetsWithRetryCount(List<String> paths, int retryCount) {
        String sql = "INSERT INTO image_recycle_bin (path, retry_count) VALUES (:path, :retryCount)";
        SqlParameterSource[] args = paths.stream()
                .map(path -> new MapSqlParameterSource()
                        .addValue("path", path)
                        .addValue("retryCount", retryCount))
                .toArray(SqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(sql, args);
    }

    @TestConfiguration
    static class SqliteDataSourcePropertiesConfig {

        @Bean
        @ConfigurationProperties(prefix = "spring.sqlite-datasource")
        public DataSourceProperties sqliteDataSourceProperties() {
            return new DataSourceProperties();
        }

        @Bean
        public DataSource sqliteDataSource(DataSourceProperties sqliteDataSourceProperties) {
            return sqliteDataSourceProperties
                    .initializeDataSourceBuilder()
                    .driverClassName(sqliteDataSourceProperties.getDriverClassName())
                    .url("jdbc:sqlite:test-file.db?foreign_keys=on&busy_timeout=3000&journal_mode=WAL&synchronous=NORMAL")
                    .type(HikariDataSource.class)
                    .build();
        }

        @Bean
        public PlatformTransactionManager transactionManager(DataSource sqliteDataSource) {
            return new JdbcTransactionManager(sqliteDataSource);
        }
    }
}
