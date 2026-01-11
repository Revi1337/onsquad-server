package revi1337.onsquad.infrastructure.aws.s3;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class S3ThreadPoolConfig {

    /**
     * Worker Thread Used For {@link FileDeleteEventListener}
     */
    @Bean("fileDeletionRecorder")
    public Executor fileDeletionRecorder() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("recorder-fdel-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    /**
     * Worker Thread Used For {@link S3StorageCleaner}
     */
    @Bean(name = "s3DeletionExecutor")
    public Executor s3DeletionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(0);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(0);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("s3-cleaner-");
        executor.initialize();
        return executor;
    }
}
