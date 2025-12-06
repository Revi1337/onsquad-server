package revi1337.onsquad.announce.application.initializer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.common.TestContainerSupport;

@ActiveProfiles("local")
@ImportAutoConfiguration(RedisAutoConfiguration.class)
@ContextConfiguration(classes = AnnounceCacheDestroyer.class)
@ExtendWith(SpringExtension.class)
class AnnounceCacheDestroyerTest extends TestContainerSupport {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("Local 환경에서 ConditionalAnnounceCacheDestroyer Bean 생성되는지 검증한다.")
    void success() {
        assertThat(applicationContext.getBean(AnnounceCacheDestroyer.class))
                .isNotNull();
    }
}
