package revi1337.onsquad.announce.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

/**
 * Factory class responsible for selecting the appropriate {@link AnnounceCacheEvictor} based on the active {@link CacheManager}.
 * <p>
 * This factory implements the <b>Strategy Pattern</b> to resolve the correct eviction strategy at runtime, allowing the system to handle different cache
 * storages (e.g., Redis, Caffeine) transparently without coupling the business logic to specific cache implementations.
 * </p>
 *
 * @see AnnounceCacheEvictor
 * @see revi1337.onsquad.announce.infrastructure.RedisAnnounceCacheEvictor
 * @see revi1337.onsquad.announce.infrastructure.CaffeineAnnounceCacheEvictor
 */
@RequiredArgsConstructor
@Component
public class AnnounceCacheEvictorFactory {

    public static final String ERROR_MESSAGE_FORMAT = "No suitable AnnounceCacheEvictor found for the provided CacheManager: ";
    private final List<AnnounceCacheEvictor> cacheEvictors;

    public AnnounceCacheEvictor findAvailableEvictor(CacheManager cacheManager) {
        return cacheEvictors.stream()
                .filter(cacheEvictor -> cacheEvictor.supports(cacheManager))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException(ERROR_MESSAGE_FORMAT + cacheManager.getClass().getName()));
    }
}
