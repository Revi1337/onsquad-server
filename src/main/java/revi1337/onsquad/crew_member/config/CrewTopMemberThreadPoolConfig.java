package revi1337.onsquad.crew_member.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import revi1337.onsquad.crew_member.application.CrewTopMemberRefreshService;

@Configuration
public class CrewTopMemberThreadPoolConfig {

    /**
     * Worker Thread Used For {@link CrewTopMemberRefreshService}
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
