package revi1337.onsquad.crew_request.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static revi1337.onsquad.common.fixture.CrewFixture.createCrew;
import static revi1337.onsquad.common.fixture.MemberFixture.createAndong;
import static revi1337.onsquad.common.fixture.MemberFixture.createRevi;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import revi1337.onsquad.common.config.ApplicationLayerConfiguration;
import revi1337.onsquad.common.container.RedisTestContainerInitializer;
import revi1337.onsquad.common.error.CommonBusinessException;
import revi1337.onsquad.crew.domain.entity.Crew;
import revi1337.onsquad.crew.domain.repository.CrewJpaRepository;
import revi1337.onsquad.crew_request.domain.repository.CrewRequestJpaRepository;
import revi1337.onsquad.history.application.listener.HistoryEventListener;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;
import revi1337.onsquad.notification.application.listener.NotificationEventListener;

@Sql({"/h2-truncate.sql"})
@Import(ApplicationLayerConfiguration.class)
@ContextConfiguration(initializers = RedisTestContainerInitializer.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class CrewRequestCommandServiceThrottlingTest {

    private static final String CIRCUIT_BREAKER_NAME = "redisThrottleCircuitBreaker";

    @Autowired
    private MemberJpaRepository memberRepository;

    @Autowired
    private CrewJpaRepository crewRepository;

    @Autowired
    private CrewRequestJpaRepository crewRequestRepository;

    @Autowired
    private CrewRequestCommandService crewRequestCommandService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @SpyBean(name = "dataSource")
    private DataSource dataSource;

    @MockBean
    private HistoryEventListener historyEventListener;

    @MockBean
    private NotificationEventListener notificationEventListener;

    @BeforeEach
    void setUp() {
        stringRedisTemplate.execute((RedisCallback<Void>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
        circuitBreakerRegistry.circuitBreaker(CIRCUIT_BREAKER_NAME).reset();
    }

    @Test
    @DisplayName("같은 memberId+crewId로 연속 요청 시, 두 번째 요청은 트랜잭션 시작 전 ThrottlingAspect에서 차단되어 DB 커넥션을 점유하지 않는다")
    void test() throws SQLException {
        Member revi = memberRepository.save(createRevi());
        Member andong = memberRepository.save(createAndong());
        Crew crew = crewRepository.save(createCrew(revi));

        crewRequestCommandService.request(andong.getId(), crew.getId());

        assertThat(crewRequestRepository.findAll()).hasSize(1);
        clearInvocations(dataSource);
        assertThatThrownBy(() -> crewRequestCommandService.request(andong.getId(), crew.getId()))
                .isInstanceOf(CommonBusinessException.TooManyRequest.class);
        verify(dataSource, never()).getConnection();
    }
}
