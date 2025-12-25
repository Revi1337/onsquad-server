package revi1337.onsquad.backup.crew.application.initializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.backup.crew.application.initializer.LocalCrewTopMemberInitializerTest.TestConfig;
import revi1337.onsquad.crew.application.initializer.CrewTopMemberInitializerSelector;
import revi1337.onsquad.crew_member.application.initializer.LocalCrewTopMemberInitializer;
import revi1337.onsquad.crew_member.application.initializer.NonLocalCrewTopMemberInitializer;
import revi1337.onsquad.crew_member.config.CrewTopMemberProperties;
import revi1337.onsquad.crew_member.domain.repository.top.CrewTopMemberRepository;

@ActiveProfiles("local")
@TestPropertySource(properties = "spring.sql.init.mode=always")
@ContextConfiguration(classes = {TestConfig.class, CrewTopMemberInitializerSelector.class})
@ExtendWith(SpringExtension.class)
public class LocalCrewTopMemberInitializerTest {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @MockBean
    private CrewTopMemberRepository crewTopMemberRepository;

    @MockBean
    private CrewTopMemberProperties crewTopMemberProperties;

    @Test
    @DisplayName("local 환경의 CrewTopMemberInitializer 를 검증한다.")
    void success() {
        when(crewTopMemberProperties.during()).thenReturn(Duration.ofDays(7));
        when(crewTopMemberProperties.rankLimit()).thenReturn(5);
        ApplicationReadyEvent applicationReadyEvent = new ApplicationReadyEvent(
                new SpringApplication(), new String[]{}, applicationContext, Duration.ofMinutes(1));

        applicationEventPublisher.publishEvent(applicationReadyEvent);

        assertAll(() -> {
            assertThat(applicationContext.getBean(LocalCrewTopMemberInitializer.class)).isNotNull();
            assertThatThrownBy(() -> applicationContext.getBean(NonLocalCrewTopMemberInitializer.class))
                    .isExactlyInstanceOf(NoSuchBeanDefinitionException.class);
            verify(crewTopMemberRepository, times(1)).deleteAllInBatch();
            verify(crewTopMemberRepository, times(1)).insertBatch(anyList());
        });
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public CrewTopMemberRepository crewTopMemberRepository() {
            return mock(CrewTopMemberRepository.class);
        }

        @Bean
        public CrewTopMemberProperties crewTopMemberProperty() {
            return mock(CrewTopMemberProperties.class);
        }
    }
}
