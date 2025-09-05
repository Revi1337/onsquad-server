package revi1337.onsquad.common.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestCacheHandlerExecutionChainTest {

    @Test
    @DisplayName("RequestCacheHandlerExecutionChain addFirst()에 성공한다. (1)")
    void addFirst1() {
        RequestCacheHandlerExecutionChain cacheHandlerChain = new RequestCacheHandlerExecutionChain();

        cacheHandlerChain.addFirst(mock(ExpiringMapRequestCacheHandler.class));

        assertThat(cacheHandlerChain.getRequestCacheHandlers()).hasSize(1);
        assertThat(cacheHandlerChain.getRequestCacheHandlers())
                .first()
                .isExactlyInstanceOf(ExpiringMapRequestCacheHandler.class);
    }

    @Test
    @DisplayName("RequestCacheHandlerExecutionChain addFirst()에 성공한다. (2)")
    void addFirst2() {
        RequestCacheHandlerExecutionChain cacheHandlerChain = new RequestCacheHandlerExecutionChain();
        cacheHandlerChain.addFirst(mock(RedisRequestCacheHandler.class));

        cacheHandlerChain.addFirst(mock(ExpiringMapRequestCacheHandler.class));

        assertThat(cacheHandlerChain.getRequestCacheHandlers()).hasSize(2);
        assertThat(cacheHandlerChain.getRequestCacheHandlers())
                .first()
                .isExactlyInstanceOf(ExpiringMapRequestCacheHandler.class);
    }

    @Test
    @DisplayName("RequestCacheHandlerExecutionChain addLast()에 성공한다. (1)")
    void addLast1() {
        RequestCacheHandlerExecutionChain cacheHandlerChain = new RequestCacheHandlerExecutionChain();

        cacheHandlerChain.addLast(mock(RedisRequestCacheHandler.class));

        assertThat(cacheHandlerChain.getRequestCacheHandlers()).hasSize(1);
        assertThat(cacheHandlerChain.getRequestCacheHandlers())
                .first()
                .isExactlyInstanceOf(RedisRequestCacheHandler.class);
    }

    @Test
    @DisplayName("RequestCacheHandlerExecutionChain addLast()에 성공한다. (2)")
    void addLast2() {
        RequestCacheHandlerExecutionChain cacheHandlerChain = new RequestCacheHandlerExecutionChain();
        cacheHandlerChain.addLast(mock(RedisRequestCacheHandler.class));

        cacheHandlerChain.addLast(mock(ExpiringMapRequestCacheHandler.class));

        assertThat(cacheHandlerChain.getRequestCacheHandlers()).hasSize(2);
        assertThat(cacheHandlerChain.getRequestCacheHandlers())
                .last()
                .isExactlyInstanceOf(ExpiringMapRequestCacheHandler.class);
    }

    @Test
    @DisplayName("RequestCacheHandlerExecutionChain addBefore()에 성공한다. (1)")
    void addBefore1() {
        RequestCacheHandlerExecutionChain cacheHandlerChain = new RequestCacheHandlerExecutionChain();

        cacheHandlerChain.addBefore(mock(RedisRequestCacheHandler.class), null);

        assertThat(cacheHandlerChain.getRequestCacheHandlers()).hasSize(1);
        assertThat(cacheHandlerChain.getRequestCacheHandlers())
                .first()
                .isExactlyInstanceOf(RedisRequestCacheHandler.class);
    }

    @Test
    @DisplayName("RequestCacheHandlerExecutionChain addBefore()에 성공한다. (2)")
    void addBefore2() {
        RequestCacheHandlerExecutionChain cacheHandlerChain = new RequestCacheHandlerExecutionChain();
        cacheHandlerChain.addBefore(mock(RedisRequestCacheHandler.class), null);

        cacheHandlerChain.addBefore(mock(ExpiringMapRequestCacheHandler.class), RedisRequestCacheHandler.class);

        assertThat(cacheHandlerChain.getRequestCacheHandlers()).hasSize(2);
        assertThat(cacheHandlerChain.getRequestCacheHandlers())
                .first()
                .isExactlyInstanceOf(ExpiringMapRequestCacheHandler.class);
    }

    @Test
    @DisplayName("RequestCacheHandlerExecutionChain addAfter()에 성공한다. (1)")
    void addAfter1() {
        RequestCacheHandlerExecutionChain cacheHandlerChain = new RequestCacheHandlerExecutionChain();

        cacheHandlerChain.addAfter(mock(RedisRequestCacheHandler.class), null);

        assertThat(cacheHandlerChain.getRequestCacheHandlers()).hasSize(1);
        assertThat(cacheHandlerChain.getRequestCacheHandlers())
                .first()
                .isExactlyInstanceOf(RedisRequestCacheHandler.class);
    }

    @Test
    @DisplayName("RequestCacheHandlerExecutionChain addAfter()에 성공한다. (2)")
    void addAfter2() {
        RequestCacheHandlerExecutionChain cacheHandlerChain = new RequestCacheHandlerExecutionChain();
        cacheHandlerChain.addAfter(mock(RedisRequestCacheHandler.class), null);

        cacheHandlerChain.addAfter(mock(ExpiringMapRequestCacheHandler.class), RedisRequestCacheHandler.class);

        assertThat(cacheHandlerChain.getRequestCacheHandlers()).hasSize(2);
        assertThat(cacheHandlerChain.getRequestCacheHandlers())
                .last()
                .isExactlyInstanceOf(ExpiringMapRequestCacheHandler.class);
    }

    @Test
    @DisplayName("앞의 RequestCacheHandler 에서 null 을 반환하면 다음 RequestCacheHandler 를 실행한다.")
    void success1() {
        String testKey = "testKey";
        String testValue = "testValue";
        long timeout = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        RequestCacheHandlerExecutionChain cacheHandlerChain = new RequestCacheHandlerExecutionChain();
        RequestCacheHandler mockRedisCacheHandler = mock(RedisRequestCacheHandler.class);
        RequestCacheHandler mockTestCacheHandler = mock(RequestCacheHandler.class);
        cacheHandlerChain.addBefore(mockRedisCacheHandler, ExpiringMapRequestCacheHandler.class);
        cacheHandlerChain.addBefore(mockTestCacheHandler, RedisRequestCacheHandler.class);
        when(mockTestCacheHandler.isFirstRequest(testKey, testValue, timeout, timeUnit)).thenReturn(null);
        when(mockRedisCacheHandler.isFirstRequest(testKey, testValue, timeout, timeUnit)).thenReturn(true);

        cacheHandlerChain.isFirstRequest(testKey, testValue, timeout, timeUnit);

        verify(mockRedisCacheHandler, times(1)).isFirstRequest(testKey, testValue, timeout, timeUnit);
        verify(mockTestCacheHandler, times(1)).isFirstRequest(testKey, testValue, timeout, timeUnit);
    }

    @Test
    @DisplayName("모든 RequestCacheHandler 들이 null을 반환하면 예외가 발생한다.")
    void success2() {
        String testKey = "testKey";
        String testValue = "testValue";
        long timeout = 5;
        TimeUnit timeUnit = TimeUnit.SECONDS;
        RequestCacheHandlerExecutionChain cacheHandlerChain = new RequestCacheHandlerExecutionChain();
        RequestCacheHandler mockRedisCacheHandler = mock(RedisRequestCacheHandler.class);
        RequestCacheHandler mockTestCacheHandler = mock(RequestCacheHandler.class);
        cacheHandlerChain.addBefore(mockRedisCacheHandler, ExpiringMapRequestCacheHandler.class);
        cacheHandlerChain.addBefore(mockTestCacheHandler, RedisRequestCacheHandler.class);
        when(mockTestCacheHandler.isFirstRequest(testKey, testValue, timeout, timeUnit)).thenReturn(null);
        when(mockRedisCacheHandler.isFirstRequest(testKey, testValue, timeout, timeUnit)).thenReturn(null);

        assertThatThrownBy(() -> cacheHandlerChain.isFirstRequest(testKey, testValue, timeout, timeUnit))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        verify(mockTestCacheHandler, times(1)).isFirstRequest(testKey, testValue, timeout, timeUnit);
        verify(mockRedisCacheHandler, times(1)).isFirstRequest(testKey, testValue, timeout, timeUnit);
    }
}
