package revi1337.onsquad.common.aspect;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import revi1337.onsquad.common.TestContainerSupport;
import revi1337.onsquad.common.config.TestObjectMapperConfiguration;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;

@ImportAutoConfiguration({RedisAutoConfiguration.class, JacksonAutoConfiguration.class})
@ContextConfiguration(classes = {RedisCacheAspect.class, TestObjectMapperConfiguration.class})
@ExtendWith(SpringExtension.class)
class RedisCacheAspectTest extends TestContainerSupport {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisCacheAspect redisCacheAspect;

    private TestService testService;

    @BeforeEach
    void setUp() {
        TestService testService = new TestService();
        AspectJProxyFactory aspectJProxyFactory = new AspectJProxyFactory(testService);
        aspectJProxyFactory.setProxyTargetClass(true);
        aspectJProxyFactory.addAspect(redisCacheAspect);
        this.testService = aspectJProxyFactory.getProxy();
    }

    @AfterEach
    void tearDown() {
        stringRedisTemplate.getConnectionFactory()
                .getConnection()
                .serverCommands()
                .flushAll();
    }

    @Nested
    @DisplayName("String 캐싱을 테스트한다.")
    class StringCache {

        @Test
        @DisplayName("캐싱할 값이 String 이고 처음 시도하는 캐시 요청이면 Redis 에 캐싱한다.")
        void success1() throws Exception {
            Long crewId = 1L;
            Long memberId = 2L;
            String cacheKey = generateCacheKey(crewId, memberId);
            String cacheValue = "test-cacheValue";

            testService.stringMethod(crewId, memberId, cacheValue);

            Method method = TestService.class.getMethod("stringMethod", Long.class, Long.class, String.class);
            JavaType javaType = objectMapper.getTypeFactory().constructType(method.getGenericReturnType());
            String redisValue = objectMapper.readValue(stringRedisTemplate.opsForValue().get(cacheKey), javaType);
            assertThat(redisValue).isEqualTo(cacheValue);
        }

        @Test
        @DisplayName("캐싱할 값이 String 이고 이미 캐싱된 값이 있으면 Redis 에 캐싱하지 않는다.")
        void success2() {
            Long crewId = 1L;
            Long memberId = 2L;
            String cacheValue1 = "test-cacheValue-1";
            String cacheValue2 = "test-cacheValue-2";
            testService.stringMethod(crewId, memberId, cacheValue1);

            String value = testService.stringMethod(crewId, memberId, cacheValue2);

            assertThat(value).isEqualTo(cacheValue1);
        }
    }

    @Nested
    @DisplayName("List 캐싱을 테스트한다.")
    class ListCache {

        @Test
        @DisplayName("캐싱할 값이 List 이고 처음 시도하는 캐시 요청이면 Redis 에 캐싱한다.")
        void success1() throws Exception {
            Long crewId = 1L;
            Long memberId = 2L;
            String cacheKey = generateListCacheKey(crewId, memberId);
            List<Object> cacheValue = List.of("a", "b");

            testService.listMethod(crewId, memberId, cacheValue);

            Method method = TestService.class.getMethod("listMethod", Long.class, Long.class, List.class);
            JavaType javaType = objectMapper.getTypeFactory().constructType(method.getGenericReturnType());
            List<Object> redisValue = objectMapper.readValue(stringRedisTemplate.opsForValue().get(cacheKey), javaType);
            assertThat(redisValue).contains("a", "b");
        }

        @Test
        @DisplayName("캐싱할 값이 List 이고 이미 캐싱된 값이 있으면 Redis 에 캐싱하지 않는다.")
        void success2() {
            Long crewId = 1L;
            Long memberId = 2L;
            List<Object> cacheValue1 = List.of("a", "b");
            List<Object> cacheValue2 = List.of("c", "d");
            testService.listMethod(crewId, memberId, cacheValue1);

            List<Object> value = testService.listMethod(crewId, memberId, cacheValue2);

            assertThat(value).contains("a", "b");
        }
    }

    private String generateCacheKey(Long crewId, Long memberId) {
        return String.format(
                CacheFormat.COMPLEX, "test-return-value-cache", "crew:" + crewId + ":member:" + memberId
        );
    }

    private String generateListCacheKey(Long crewId, Long memberId) {
        return String.format(
                CacheFormat.COMPLEX, "test-return-list-cache", "crew:" + crewId + ":member:" + memberId
        );
    }

    static class TestService {

        @RedisCache(name = "test-return-value-cache", key = "'crew:' + #crewId + ':member:' + #memberId", ttl = 5, unit = TimeUnit.SECONDS)
        public String stringMethod(Long crewId, Long memberId, String value) {
            return value;
        }

        @RedisCache(name = "test-return-list-cache", key = "'crew:' + #crewId + ':member:' + #memberId", ttl = 5, unit = TimeUnit.SECONDS)
        public List<Object> listMethod(Long crewId, Long memberId, List<Object> lists) {
            return lists;
        }
    }
}
