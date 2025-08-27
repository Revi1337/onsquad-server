package revi1337.onsquad.inrastructure.mail.support;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.inrastructure.mail.repository.VerificationCodeExpiringMapRepository;

@ContextConfiguration(classes = {VerificationCacheLifeCycleManager.class, VerificationCodeExpiringMapRepository.class})
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@ExtendWith(SpringExtension.class)
class VerificationCacheLifeCycleManagerEventTest {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @SpyBean
    private VerificationCacheLifeCycleManager lifeCycleManager;

    @Test
    @DisplayName("스프링이 뜰 때, restore 가 실행되면 성공한다.")
    void success1() {
        doNothing().when(lifeCycleManager).restore();

        eventPublisher.publishEvent(new ApplicationReadyEvent(new SpringApplication(), new String[]{}, applicationContext, null));

        verify(lifeCycleManager, times(1)).restore();
    }

    @Test
    @DisplayName("스프링이 죽을 때, backup 가 실행되면 성공한다.")
    void success2() {
        doNothing().when(lifeCycleManager).backup(any(ContextClosedEvent.class));

        eventPublisher.publishEvent(new ContextClosedEvent(applicationContext));

        verify(lifeCycleManager, times(1)).backup(any(ContextClosedEvent.class));
    }
}
