package revi1337.onsquad.infrastructure.network.mail;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableConfigurationProperties(MailProperties.class)
@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender(MailProperties mailProperties) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(mailProperties.getHost());
        javaMailSender.setPort(mailProperties.getPort());
        javaMailSender.setUsername(mailProperties.getUsername());
        javaMailSender.setPassword(mailProperties.getPassword());
        javaMailSender.setDefaultEncoding(StandardCharsets.UTF_8.name());
        javaMailSender.setJavaMailProperties(getMailProperties(mailProperties));
        return javaMailSender;
    }

    @Bean("sending-verification-code-executor")
    public ThreadPoolTaskExecutor verificationCodeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("code-sender");
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(30);
        executor.setQueueCapacity(100);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }

    private Properties getMailProperties(MailProperties mailProperties) {
        Properties properties = new Properties();
        properties.putAll(mailProperties.getProperties());
        return properties;
    }
}
