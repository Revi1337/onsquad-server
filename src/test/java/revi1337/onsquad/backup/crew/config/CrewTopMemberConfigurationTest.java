package revi1337.onsquad.backup.crew.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.internal.util.MockUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.backup.crew.application.initializer.LocalCrewTopMemberInitializer;
import revi1337.onsquad.backup.crew.application.initializer.NonLocalCrewTopMemberInitializer;
import revi1337.onsquad.backup.crew.domain.repository.CrewTopMemberRepository;

class CrewTopMemberConfigurationTest {

    @Nested
    @ActiveProfiles("local")
    @TestPropertySource(properties = "spring.sql.init.mode=always")
    @ContextConfiguration(classes = {TestConfig.class, LocalCrewTopMemberInitializer.class})
    @ExtendWith(SpringExtension.class)
    class LocalBeanTest {

        @Autowired
        private ConfigurableApplicationContext applicationContext;

        @Test
        @DisplayName("실행환경이 local 인지 검증한다.")
        void success1() {
            Environment environment = applicationContext.getEnvironment();

            String[] activeProfiles = environment.getActiveProfiles();

            assertThat(activeProfiles).contains("local");
        }

        @Test
        @DisplayName("local 환경에서 LocalCrewTopMemberInitializer 가 생성되는지 검증한다.")
        void success2() {
            LocalCrewTopMemberInitializer initializer = applicationContext.getBean(LocalCrewTopMemberInitializer.class);

            assertThat(initializer).isNotNull();
            assertThat(MockUtil.isMock(initializer)).isFalse();
        }

        @Test
        @DisplayName("local 환경에서 NonLocalCrewTopMemberInitializer 가 생성되면 안된다.")
        void success3() {
            assertThatThrownBy(() -> applicationContext.getBean(NonLocalCrewTopMemberInitializer.class))
                    .isExactlyInstanceOf(NoSuchBeanDefinitionException.class);
        }
    }

    @Nested
    @ActiveProfiles("dev")
    @ContextConfiguration(classes = {TestConfig.class, NonLocalCrewTopMemberInitializer.class})
    @ExtendWith(SpringExtension.class)
    class NonLocalBeanTest {

        @Autowired
        private ConfigurableApplicationContext applicationContext;

        @Test
        @DisplayName("실행환경이 dev 인지 검증한다.")
        void success1() {
            Environment environment = applicationContext.getEnvironment();

            String[] activeProfiles = environment.getActiveProfiles();

            assertThat(activeProfiles).contains("dev");
        }

        @Test
        @DisplayName("dev 환경에서 NonLocalCrewTopMemberInitializer 가 생성되는지 검증한다.")
        void success2() {
            NonLocalCrewTopMemberInitializer initializer = applicationContext
                    .getBean(NonLocalCrewTopMemberInitializer.class);

            assertThat(initializer).isNotNull();
            assertThat(MockUtil.isMock(initializer)).isFalse();
        }

        @Test
        @DisplayName("dev 환경에서 LocalCrewTopMemberInitializer 가 생성되면 안된다.")
        void success3() {
            assertThatThrownBy(() -> applicationContext.getBean(LocalCrewTopMemberInitializer.class))
                    .isExactlyInstanceOf(NoSuchBeanDefinitionException.class);
        }
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
