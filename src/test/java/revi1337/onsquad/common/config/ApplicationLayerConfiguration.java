package revi1337.onsquad.common.config;

import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCE;
import static revi1337.onsquad.common.constant.CacheConst.CREW_ANNOUNCES;
import static revi1337.onsquad.common.constant.CacheConst.CREW_STATISTIC;
import static revi1337.onsquad.common.constant.CacheConst.CREW_TOP_USERS;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import revi1337.onsquad.infrastructure.aws.s3.S3BucketProperties;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
public class ApplicationLayerConfiguration {

    @Bean
    public HandlerExceptionResolver handlerExceptionResolver() {
        return new ExceptionHandlerExceptionResolver();
    }

    @Bean
    public CacheManager concurrentMapCacheManager() {
        return new ConcurrentMapCacheManager(
                CREW_ANNOUNCES,
                CREW_ANNOUNCE,
                CREW_STATISTIC,
                CREW_TOP_USERS,
                CREW_TOP_USERS
        );
    }

    @Primary
    @Bean
    public S3Client amazonS3(S3BucketProperties s3BucketProperties,
                             @Value("${s3.endpoint:http://dummy-entry}") String endpoint) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                s3BucketProperties.accessKey(), s3BucketProperties.secretKey()
        );

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(s3BucketProperties.region()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .forcePathStyle(true)
                .build();
    }
}
