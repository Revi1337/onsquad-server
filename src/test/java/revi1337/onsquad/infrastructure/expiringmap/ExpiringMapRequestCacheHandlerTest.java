package revi1337.onsquad.infrastructure.expiringmap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;
import net.jodah.expiringmap.ExpiringMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ExpiringMapRequestCacheHandlerTest {

    private final ExpiringMapRequestCacheHandler expiringMapRequestCacheHandler = new ExpiringMapRequestCacheHandler();

    @BeforeEach
    void setUp() {
        ExpiringMap<String, String> requestCache = (ExpiringMap<String, String>) ReflectionTestUtils
                .getField(ExpiringMapRequestCacheHandler.class, "REQUEST_CACHE");
        requestCache.clear();
    }

    @Test
    @DisplayName("처음 시도하는 요청이면 true 를 반환한다.")
    void success1() {
        String testKey = "testKey";
        String testValue = "testValue";
        long timeout = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;

        Boolean firstRequest = expiringMapRequestCacheHandler.isFirstRequest(testKey, testValue, timeout, timeUnit);

        assertThat(firstRequest).isTrue();
    }

    @Test
    @DisplayName("만료시간 안에 2번 이상 동일한 요청을 보내면 false 를 반환한다.")
    void success2() {
        String testKey = "testKey";
        String testValue = "testValue";
        long timeout = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        expiringMapRequestCacheHandler.isFirstRequest(testKey, testValue, timeout, timeUnit);

        Boolean firstRequest = expiringMapRequestCacheHandler.isFirstRequest(testKey, testValue, timeout, timeUnit);

        assertThat(firstRequest).isFalse();
    }
}
