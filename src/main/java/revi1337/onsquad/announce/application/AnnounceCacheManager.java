package revi1337.onsquad.announce.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import revi1337.onsquad.announce.application.dto.AnnounceInfoDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AnnounceCacheManager implements CacheManager {

    private static final String KEY_FORMAT = "onsquad:crew:%d:announces";
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void cacheSpecificCrewAnnounceInfos(Long crewId, List<AnnounceInfoDto> announceInfos) {
        log.debug("[Caching crew announces] : crew_id = {}", crewId);
        String cacheKey = String.format(KEY_FORMAT, crewId);
        redisTemplate.opsForValue().set(cacheKey, announceInfos);
    }

    @Override
    public List<AnnounceInfoDto> getCachedCrewAnnounceInfosById(Long crewId) {
        String cacheKey = String.format(KEY_FORMAT, crewId);
        return Optional.ofNullable(redisTemplate.opsForValue().get(cacheKey))
                .filter(List.class::isInstance)
                .map(List.class::cast)
                .map(list -> {
                    log.debug("[Cache Hits crew announces] : crew_id = {}", crewId);
                    return ((List<?>) list).stream()
                            .map(data -> objectMapper.convertValue(data, AnnounceInfoDto.class))
                            .collect(Collectors.toList());
                })
                .orElseGet(() -> {
                    log.debug("[Cache Miss crew announces] : crew_id = {}", crewId);
                    return new ArrayList<>();
                });
    }
}
