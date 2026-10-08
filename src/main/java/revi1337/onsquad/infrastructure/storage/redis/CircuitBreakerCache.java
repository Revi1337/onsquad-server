package revi1337.onsquad.infrastructure.storage.redis;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import java.util.concurrent.Callable;
import org.springframework.cache.Cache;

/**
 * Decorator that guards every Redis I/O of the delegate {@link Cache} with a {@link CircuitBreaker}.
 * <p>
 * Exceptions (including {@code CallNotPermittedException} while the circuit is OPEN) are rethrown as-is. Swallowing them is the responsibility of the
 * {@link org.springframework.cache.interceptor.CacheErrorHandler}.
 *
 * @see RedisCacheErrorHandler
 */
public class CircuitBreakerCache implements Cache {

    private final Cache delegate;
    private final CircuitBreaker circuitBreaker;

    public CircuitBreakerCache(Cache delegate, CircuitBreaker circuitBreaker) {
        this.delegate = delegate;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Object getNativeCache() {
        return delegate.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        return circuitBreaker.executeSupplier(() -> delegate.get(key));
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return circuitBreaker.executeSupplier(() -> delegate.get(key, type));
    }

    /**
     * The {@code valueLoader} (DB access) must not run inside the circuit breaker, otherwise its failures would be counted as Redis failures. So lookup and
     * store are guarded separately and the loader is invoked outside of them.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper cached = get(key);
        if (cached != null) {
            return (T) cached.get();
        }

        T value;
        try {
            value = valueLoader.call();
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
        put(key, value);
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        circuitBreaker.executeRunnable(() -> delegate.put(key, value));
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return circuitBreaker.executeSupplier(() -> delegate.putIfAbsent(key, value));
    }

    @Override
    public void evict(Object key) {
        circuitBreaker.executeRunnable(() -> delegate.evict(key));
    }

    @Override
    public boolean evictIfPresent(Object key) {
        return circuitBreaker.executeSupplier(() -> delegate.evictIfPresent(key));
    }

    @Override
    public void clear() {
        circuitBreaker.executeRunnable(delegate::clear);
    }

    @Override
    public boolean invalidate() {
        return circuitBreaker.executeSupplier(delegate::invalidate);
    }
}
