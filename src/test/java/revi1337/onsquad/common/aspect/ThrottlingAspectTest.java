package revi1337.onsquad.common.aspect;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import revi1337.onsquad.common.ApplicationLayerTestSupport;
import revi1337.onsquad.common.ApplicationLayerWithTestContainerSupport;
import revi1337.onsquad.common.error.CommonBusinessException;
import revi1337.onsquad.infrastructure.expiringmap.ExpiringMapRequestCacheHandler;
import revi1337.onsquad.infrastructure.redis.RedisCacheCleaner;
import revi1337.onsquad.infrastructure.redis.RedisRequestCacheHandler;

class ThrottlingAspectTest {

    @Nested
    @DisplayName("ExpiringMap을 이용한 ThrottlingAspectTest")
    class ExpiringMapThrottlingAspectTest extends ApplicationLayerTestSupport {

        private final ThrottlingAspect throttlingAspect;
        private TestService testService;

        @Autowired
        public ExpiringMapThrottlingAspectTest(ExpiringMapRequestCacheHandler requestCacheHandler) {
            this.throttlingAspect = new ThrottlingAspect(requestCacheHandler);
        }

        @BeforeEach
        void setUp() {
            TestService testService = new TestService();
            AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(testService);
            aspectJProxyFactory.setProxyTargetClass(true);
            aspectJProxyFactory.addAspect(throttlingAspect);
            this.testService = aspectJProxyFactory.getProxy();
        }

        @Test
        @DisplayName("처음 시도하는 요청이면 예외가 발생하지 않는다.")
        void success() {
            Long crewId = 1L;
            Long memberId = 2L;

            testService.testMethod(crewId, memberId);
        }

        @Test
        @DisplayName("만료시간 안에 2번 이상 동일한 요청을 보내면 false 를 반환한다.")
        void fail() {
            Long crewId = 2L;
            Long memberId = 3L;
            testService.testMethod(crewId, memberId);

            assertThatThrownBy(() -> testService.testMethod(crewId, memberId))
                    .isExactlyInstanceOf(CommonBusinessException.RequestConflict.class);
        }
    }

    @Nested
    @DisplayName("Redis를 이용한 ThrottlingAspectTest")
    class RedisThrottlingAspectTest extends ApplicationLayerWithTestContainerSupport {

        private final StringRedisTemplate stringRedisTemplate;
        private final ThrottlingAspect throttlingAspect;
        private TestService testService;

        @Autowired
        public RedisThrottlingAspectTest(RedisRequestCacheHandler requestCacheHandler, StringRedisTemplate stringRedisTemplate) {
            this.stringRedisTemplate = stringRedisTemplate;
            this.throttlingAspect = new ThrottlingAspect(requestCacheHandler);
        }

        @BeforeEach
        void setUp() {
            TestService testService = new TestService();
            AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(testService);
            aspectJProxyFactory.setProxyTargetClass(true);
            aspectJProxyFactory.addAspect(throttlingAspect);
            this.testService = aspectJProxyFactory.getProxy();
            RedisCacheCleaner.cleanAll(stringRedisTemplate);
        }

        @Test
        @DisplayName("처음 시도하는 요청이면 예외가 발생하지 않는다.")
        void success() {
            Long crewId = 1L;
            Long memberId = 2L;

            testService.testMethod(crewId, memberId);
        }

        @Test
        @DisplayName("만료시간 안에 2번 이상 동일한 요청을 보내면 false 를 반환한다.")
        void fail() {
            Long crewId = 2L;
            Long memberId = 3L;
            testService.testMethod(crewId, memberId);

            assertThatThrownBy(() -> testService.testMethod(crewId, memberId))
                    .isExactlyInstanceOf(CommonBusinessException.RequestConflict.class);
        }
    }

    @Nested
    @DisplayName("Chain을 이용한 ThrottlingAspectTest")
    class ChainThrottlingAspectTest extends ApplicationLayerWithTestContainerSupport {

        private final StringRedisTemplate stringRedisTemplate;
        private final ThrottlingAspect throttlingAspect;
        private TestService testService;

        @Autowired
        public ChainThrottlingAspectTest(
                ExpiringMapRequestCacheHandler expiringMapRequestCacheHandler,
                RedisRequestCacheHandler redisRequestCacheHandler,
                StringRedisTemplate stringRedisTemplate
        ) {
            this.stringRedisTemplate = stringRedisTemplate;
            this.throttlingAspect = new ThrottlingAspect(new RequestCacheHandlerComposite(List.of(redisRequestCacheHandler, expiringMapRequestCacheHandler)));
        }

        @BeforeEach
        void setUp() {
            TestService testService = new TestService();
            AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(testService);
            aspectJProxyFactory.setProxyTargetClass(true);
            aspectJProxyFactory.addAspect(throttlingAspect);
            this.testService = aspectJProxyFactory.getProxy();
            RedisCacheCleaner.cleanAll(stringRedisTemplate);
        }

        @Test
        @DisplayName("처음 시도하는 요청이면 예외가 발생하지 않는다.")
        void success() {
            Long crewId = 1L;
            Long memberId = 2L;

            testService.testMethod(crewId, memberId);
        }

        @Test
        @DisplayName("만료시간 안에 2번 이상 동일한 요청을 보내면 false 를 반환한다.")
        void fail() {
            Long crewId = 2L;
            Long memberId = 3L;
            testService.testMethod(crewId, memberId);

            assertThatThrownBy(() -> testService.testMethod(crewId, memberId))
                    .isExactlyInstanceOf(CommonBusinessException.RequestConflict.class);
        }
    }

    static class TestService {

        @Throttling(name = "test-request-cache", key = "'crew:' + #crewId + ':member:' + #memberId", during = 5)
        public void testMethod(Long crewId, Long memberId) {
            // ignored
        }
    }
}
