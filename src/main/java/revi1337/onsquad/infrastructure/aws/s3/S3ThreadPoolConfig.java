package revi1337.onsquad.infrastructure.aws.s3;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class S3ThreadPoolConfig {

    /**
     * Worker Thread Used For {@link S3StorageCleaner}
     */
    @Bean(name = "s3DeletionExecutor")
    public Executor s3DeletionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("s3-cleaner-");
        executor.initialize();
        return executor;
    }

    /**
     * Worker Thread Used For {@link revi1337.onsquad.infrastructure.aws.s3.event.FileDeleteEventListener}
     */
    @Bean("fileDeleteEventExecutor")
    public Executor fileDeleteEventExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1);
        taskExecutor.setMaxPoolSize(1);
        taskExecutor.setQueueCapacity(500);
        taskExecutor.setThreadNamePrefix("f-event-exec-");
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }
}
