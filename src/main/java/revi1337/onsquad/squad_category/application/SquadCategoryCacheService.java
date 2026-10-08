package revi1337.onsquad.squad_category.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.common.util.ObjectMapperUtils;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;
import revi1337.onsquad.squad_category.domain.model.SimpleSquadCategory;
import revi1337.onsquad.squad_category.domain.model.SquadCategories;

@Slf4j
@Service
public class SquadCategoryCacheService {

    private static final String REDIS_CACHE_CIRCUIT_BREAKER_NAME = "redisCacheCircuitBreaker";
    private static final String SQUAD_CATEGORY_KEY_FORMAT = "squad:%s:categories";
    private static final Duration SQUAD_CATEGORY_TTL = Duration.ofHours(6);
    private static final String CACHE_ERROR_LOG_FORMAT = "[Squad Category Cache {} 실패] {}: {}";
    private static final String CACHE_BLOCKED_LOG_FORMAT = "[Squad Category Cache {} 차단] {}";

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper defaultObjectMapper;
    private final SquadCategoryAccessor squadCategoryAccessor;
    private final CircuitBreaker circuitBreaker;

    public SquadCategoryCacheService(
            StringRedisTemplate stringRedisTemplate,
            ObjectMapper defaultObjectMapper,
            SquadCategoryAccessor squadCategoryAccessor,
            CircuitBreakerRegistry circuitBreakerRegistry
    ) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.defaultObjectMapper = defaultObjectMapper;
        this.squadCategoryAccessor = squadCategoryAccessor;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(REDIS_CACHE_CIRCUIT_BREAKER_NAME);
    }

    public SquadCategories getCategoriesBySquadIdIn(List<Long> squadIds) {
        List<String> computedKeys = generateCacheKeys(squadIds);
        List<String> serializedValues = executeQuietly("get", () -> stringRedisTemplate.opsForValue().multiGet(computedKeys), null);

        List<SimpleSquadCategory> totalCategories = new ArrayList<>();
        List<Long> missSquadIds = new ArrayList<>();
        classifyCacheResults(squadIds, missSquadIds, serializedValues, totalCategories);

        if (!missSquadIds.isEmpty()) {
            totalCategories.addAll(processCacheMiss(missSquadIds));
        }
        return new SquadCategories(totalCategories);
    }

    public void evictSquadCategories(List<Long> squadIds) {
        List<String> computedKeys = generateCacheKeys(squadIds);
        executeQuietly("evict", () -> {
            RedisCacheEvictor.unlinkKeys(stringRedisTemplate, computedKeys);
            return null;
        }, null);
    }

    private String generateCacheKey(Long squadId) {
        String key = String.format(SQUAD_CATEGORY_KEY_FORMAT, squadId);
        return String.format(CacheFormat.SIMPLE, key);
    }

    private List<String> generateCacheKeys(List<Long> squadIds) {
        return squadIds.stream()
                .map(this::generateCacheKey)
                .toList();
    }

    private void classifyCacheResults(List<Long> squadIds, List<Long> missSquadIds, List<String> serializedValues, List<SimpleSquadCategory> totalCategories) {
        for (int i = 0; i < squadIds.size(); i++) {
            String json = (serializedValues != null) ? serializedValues.get(i) : null;
            SquadCategories cached = (json != null) ? ObjectMapperUtils.deserialize(defaultObjectMapper, json, SquadCategories.class) : null;
            if (cached != null) {
                totalCategories.addAll(cached.values());
            } else {
                missSquadIds.add(squadIds.get(i));
            }
        }
    }

    private List<SimpleSquadCategory> processCacheMiss(List<Long> missSquadIds) {
        SquadCategories missedCategories = squadCategoryAccessor.fetchCategoriesBySquadIdIn(missSquadIds);
        Map<Long, SquadCategories> splitGroup = missedCategories.splitBySquad();
        Map<String, String> serializedEntries = new LinkedHashMap<>();
        missSquadIds.forEach(missSquadId -> serializedEntries.put(
                generateCacheKey(missSquadId),
                ObjectMapperUtils.serializeToString(defaultObjectMapper, splitGroup.getOrDefault(missSquadId, new SquadCategories()))
        ));
        executeQuietly("put", () -> stringRedisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            serializedEntries.forEach((key, value) -> {
                byte[] serializedKey = stringRedisTemplate.getStringSerializer().serialize(key);
                byte[] serializedValue = stringRedisTemplate.getStringSerializer().serialize(value);

                connection.stringCommands().set(serializedKey, serializedValue, Expiration.from(SQUAD_CATEGORY_TTL), SetOption.upsert());
            });
            return null;
        }), null);

        return missedCategories.values();
    }

    private <T> T executeQuietly(String operation, Supplier<T> redisCall, T fallback) {
        try {
            return circuitBreaker.executeSupplier(redisCall);
        } catch (CallNotPermittedException exception) {
            log.warn(CACHE_BLOCKED_LOG_FORMAT, operation, exception.getMessage());
        } catch (RuntimeException exception) {
            log.error(CACHE_ERROR_LOG_FORMAT, operation, exception.getClass().getSimpleName(), exception.getMessage(), exception);
        }
        return fallback;
    }
}
