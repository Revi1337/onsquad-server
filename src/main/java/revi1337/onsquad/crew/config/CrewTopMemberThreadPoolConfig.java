package revi1337.onsquad.crew.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class CrewTopMemberThreadPoolConfig {

    /**
     * Worker Thread Used For {@link revi1337.onsquad.crew.application.CrewTopMemberRefreshService}
     */
    @Bean(name = "crewTopMemberRefreshExecutor")
    public Executor crewTopMemberRefreshExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("crew-refresh-");
        executor.initialize();
        return executor;
    }
}
