package revi1337.onsquad.crew_member.application.leaderboard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew_member.domain.model.CrewRankerDetail;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;
import revi1337.onsquad.infrastructure.storage.redis.RedisScanUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrewLeaderboardBackupManager {

    private static final Duration BACKUP_EXPIRATION = Duration.ofDays(14);
    private static final int CHUNK_SIZE = 500;

    private final ObjectMapper defaultObjectMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final CrewRankerRepository crewRankerRepository;

    public void backupCurrentTopRankers() {
        List<CrewRankerDetail> previousRankers = crewRankerRepository.findAll().stream()
                .map(CrewRankerDetail::from)
                .toList();

        if (previousRankers.isEmpty()) {
            log.info("[Backup] No existing data to archive.");
            return;
        }

        saveBackup(previousRankers);
        log.info("[Backup Start] Archiving {} previous rankers to Redis.", previousRankers.size());
    }

    public List<CrewRankerDetail> getBackup() {
        List<String> backupKeys = RedisScanUtils.scanKeys(stringRedisTemplate, CrewLeaderboardKeyMapper.getPreviousLeaderboardPattern());
        if (backupKeys.isEmpty()) {
            return Collections.emptyList();
        }

        List<List<byte[]>> partitionedSerializedKeys = new ArrayList<>();
        for (int i = 0; i < backupKeys.size(); i += CHUNK_SIZE) {
            List<byte[]> keysBytes = backupKeys.subList(i, Math.min(i + CHUNK_SIZE, backupKeys.size())).stream()
                    .map(key -> stringRedisTemplate.getStringSerializer().serialize(key))
                    .toList();
            partitionedSerializedKeys.add(keysBytes);
        }

        List<Object> pipelinedResults = stringRedisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            partitionedSerializedKeys.forEach(chunk -> connection.stringCommands().mGet(chunk.toArray(new byte[0][])));
            return null;
        });

        return deserializeResults(pipelinedResults);
    }

    public void removeBackups(List<Long> crewIds) {
        List<String> backupKeys = CrewLeaderboardKeyMapper.toPreviousLeaderboardKeys(crewIds);
        RedisCacheEvictor.unlinkKeys(stringRedisTemplate, backupKeys);
    }

    private void saveBackup(List<CrewRankerDetail> previousRankedMembers) {
        Map<Long, List<CrewRankerDetail>> groupedMembers = previousRankedMembers.stream()
                .collect(Collectors.groupingBy(CrewRankerDetail::crewId));

        stringRedisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            groupedMembers.forEach((key, results) -> {
                try {
                    String previousCrewRankKey = CrewLeaderboardKeyMapper.toPreviousLeaderboardKey(key);
                    String jsonValue = defaultObjectMapper.writeValueAsString(results);
                    byte[] serializedKey = stringRedisTemplate.getStringSerializer().serialize(previousCrewRankKey);
                    byte[] serializedValue = stringRedisTemplate.getStringSerializer().serialize(jsonValue);
                    connection.stringCommands().set(serializedKey, serializedValue, Expiration.from(BACKUP_EXPIRATION), SetOption.upsert());
                } catch (IOException e) {
                    log.error("[Backup] Serialization failed for crewId: {}", key, e);
                }
            });
            return null;
        });
    }

    private List<CrewRankerDetail> deserializeResults(List<Object> rawResults) {
        return rawResults.stream()
                .flatMap(obj -> ((List<?>) obj).stream())
                .filter(Objects::nonNull)
                .map(this::parseJson)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .toList();
    }

    private List<CrewRankerDetail> parseJson(Object obj) {
        try {
            return defaultObjectMapper.readValue((String) obj, new TypeReference<>() {
            });
        } catch (IOException e) {
            log.error("[Redis Deserialization Failed]", e);
            return null;
        }
    }
}
