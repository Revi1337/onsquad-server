package revi1337.onsquad.common.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;

class RequestCacheHandlerExecutionChainTest {

    @Test
    @DisplayName("RequestCacheHandlerExecutionChain 의 기본(가장 마지막) RequestCacheHandler 는 ExpiredMapRequestCacheHandler 이다.")
    void success1() {
        RequestCacheHandlerExecutionChain cacheHandlerChain = new RequestCacheHandlerExecutionChain();

        assertThat(cacheHandlerChain.getRequestCacheHandlers()).hasSize(1);
        assertThat(cacheHandlerChain.getRequestCacheHandlers())
                .first()
                .isExactlyInstanceOf(ExpiringMapRequestCacheHandler.class);
    }

    @Test
    @DisplayName("RequestCacheHandlerExecutionChain 의 addRequestCacheHandlerBefore 를 호출하면 특정 Handler 앞에 위치한다.")
    void success2() {
        RequestCacheHandlerExecutionChain cacheHandlerChain = new RequestCacheHandlerExecutionChain();
        RedisRequestCacheHandler redisCacheHandler = new RedisRequestCacheHandler(mock(StringRedisTemplate.class));

        cacheHandlerChain.addRequestCacheHandlerBefore(redisCacheHandler, ExpiringMapRequestCacheHandler.class);

        assertThat(cacheHandlerChain.getRequestCacheHandlers()).hasSize(2);
        assertThat(cacheHandlerChain.getRequestCacheHandlers())
                .first()
                .isExactlyInstanceOf(RedisRequestCacheHandler.class);
    }

    @Test
    @DisplayName("앞의 RequestCacheHandler 에서 null 을 반환하면 다음 RequestCacheHandler 를 실행한다.")
    void success3() {
        String testKey = "testKey";
        String testValue = "testValue";
        long timeout = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        RequestCacheHandlerExecutionChain cacheHandlerChain = new RequestCacheHandlerExecutionChain();
        RequestCacheHandler mockRedisCacheHandler = mock(RedisRequestCacheHandler.class);
        RequestCacheHandler mockTestCacheHandler = mock(RequestCacheHandler.class);
        cacheHandlerChain.addRequestCacheHandlerBefore(mockRedisCacheHandler, ExpiringMapRequestCacheHandler.class);
        cacheHandlerChain.addRequestCacheHandlerBefore(mockTestCacheHandler, RedisRequestCacheHandler.class);
        when(mockTestCacheHandler.isFirstRequest(testKey, testValue, timeout, timeUnit)).thenReturn(null);
        when(mockRedisCacheHandler.isFirstRequest(testKey, testValue, timeout, timeUnit)).thenReturn(true);

        cacheHandlerChain.isFirstRequest(testKey, testValue, timeout, timeUnit);

        verify(mockRedisCacheHandler, times(1)).isFirstRequest(testKey, testValue, timeout, timeUnit);
        verify(mockTestCacheHandler, times(1)).isFirstRequest(testKey, testValue, timeout, timeUnit);
    }

    @Test
    @DisplayName("모든 RequestCacheHandler 들이 null 을 반환해도 결국, 기본 Handler 인 다음 ExpiredMapRequestCacheHandler 에서 null 이 아닌 값을 반환한다.")
    void success4() {
        String testKey = "testKey";
        String testValue = "testValue";
        long timeout = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        RequestCacheHandlerExecutionChain cacheHandlerChain = new RequestCacheHandlerExecutionChain();
        RequestCacheHandler mockRedisCacheHandler = mock(RedisRequestCacheHandler.class);
        RequestCacheHandler mockTestCacheHandler = mock(RequestCacheHandler.class);
        cacheHandlerChain.addRequestCacheHandlerBefore(mockRedisCacheHandler, ExpiringMapRequestCacheHandler.class);
        cacheHandlerChain.addRequestCacheHandlerBefore(mockTestCacheHandler, RedisRequestCacheHandler.class);
        when(mockTestCacheHandler.isFirstRequest(testKey, testValue, timeout, timeUnit)).thenReturn(null);
        when(mockRedisCacheHandler.isFirstRequest(testKey, testValue, timeout, timeUnit)).thenReturn(null);

        Boolean firstRequest = cacheHandlerChain.isFirstRequest(testKey, testValue, timeout, timeUnit);

        verify(mockTestCacheHandler, times(1)).isFirstRequest(testKey, testValue, timeout, timeUnit);
        verify(mockRedisCacheHandler, times(1)).isFirstRequest(testKey, testValue, timeout, timeUnit);
        assertThat(firstRequest).isNotNull();
    }
}
