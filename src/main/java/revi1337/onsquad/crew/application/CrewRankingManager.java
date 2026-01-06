package revi1337.onsquad.crew.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew.util.CrewRankKeyMapper;
import revi1337.onsquad.crew_member.domain.CrewActivity;
import revi1337.onsquad.crew_member.domain.result.CrewRankedMemberResult;
import revi1337.onsquad.infrastructure.redis.RedisScanUtils;

@Slf4j
@RequiredArgsConstructor
@Component
public class CrewRankingManager {

    private static final long MULTIPLIER = 10_000_000_000L;

    private final ObjectMapper defaultObjectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public void applyActivityScore(Long crewId, Long memberId, Instant applyAt, CrewActivity crewActivity) {
        String namedSortedSet = CrewRankKeyMapper.toCrewRankKey(crewId);
        String specificName = CrewRankKeyMapper.toMemberKey(memberId);

        double currentWeight = getCurrentWeight(namedSortedSet, specificName);
        double nextWeight = calculateNextWeight(currentWeight, crewActivity.getScore(), applyAt.getEpochSecond());

        stringRedisTemplate.opsForZSet().add(namedSortedSet, specificName, nextWeight);
    }

    public List<CrewRankedMemberResult> getRankedMembers(int rankLimit) {
        List<String> computedKeys = RedisScanUtils.scanKeys(stringRedisTemplate, CrewRankKeyMapper.getCrewRankPattern());
        if (computedKeys.isEmpty()) {
            return Collections.emptyList();
        }

        List<Object> pipelinedResults = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            computedKeys.stream()
                    .map(computedKey -> stringRedisTemplate.getStringSerializer().serialize(computedKey))
                    .forEach(serializedKey -> connection.zSetCommands().zRevRangeWithScores(serializedKey, 0, rankLimit - 1));
            return null;
        });

        List<CrewRankedMemberResult> results = new ArrayList<>(computedKeys.size() * rankLimit);
        for (int i = 0; i < computedKeys.size(); i++) {
            Object result = pipelinedResults.get(i);
            if (!(result instanceof Set<?> rawSet)) {
                continue;
            }
            Long crewId = CrewRankKeyMapper.parseCrewId(computedKeys.get(i));
            int rank = 1;
            for (Object element : rawSet) {
                if (element instanceof ZSetOperations.TypedTuple<?> tuple) {
                    TypedTuple<String> stringTuple = (TypedTuple<String>) tuple;
                    results.add(convertToResult(crewId, rank++, stringTuple));
                }
            }
        }

        return results;
    }

    public void backupPreviousRankedMembers(List<CrewRankedMemberResult> previousRankedMembers) {
        Map<Long, List<CrewRankedMemberResult>> groupedMembers = previousRankedMembers.stream()
                .collect(Collectors.groupingBy(CrewRankedMemberResult::crewId));

        stringRedisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            groupedMembers.forEach((key, results) -> {
                try {
                    String previousCrewRankKey = CrewRankKeyMapper.toPreviousCrewRankKey(key);
                    String jsonValue = defaultObjectMapper.writeValueAsString(results);
                    byte[] serializedKey = stringRedisTemplate.getStringSerializer().serialize(previousCrewRankKey);
                    byte[] serializedValue = stringRedisTemplate.getStringSerializer().serialize(jsonValue);
                    connection.stringCommands().set(serializedKey, serializedValue, Expiration.from(Duration.ofDays(14)), SetOption.upsert());
                } catch (IOException e) {
                    log.error("[Redis Backup Failed] crewId: {}", key, e);
                }
            });
            return null;
        });
    }

    public void evictCrewsLeaderboard(List<Long> crewIds) {
        List<String> namedSortedSets = CrewRankKeyMapper.toCrewRankKeys(crewIds);
        stringRedisTemplate.unlink(namedSortedSets);
    }

    private double getCurrentWeight(String namedSortedSet, String specificName) {
        Double score = stringRedisTemplate.opsForZSet().score(namedSortedSet, specificName);
        return Objects.requireNonNullElse(score, 0).doubleValue();
    }

    private double calculateNextWeight(double currentWeight, int weight, long epochSecond) {
        long currentPureScore = (long) (currentWeight / MULTIPLIER);
        long nextPureScore = currentPureScore + weight;

        return ((double) nextPureScore * MULTIPLIER) + epochSecond;
    }

    private CrewRankedMemberResult convertToResult(Long crewId, int rank, TypedTuple<String> tuple) {
        double compositeScore = tuple.getScore();
        long rawScore = (long) (compositeScore / MULTIPLIER);
        long epochSecond = (long) (compositeScore - (rawScore * MULTIPLIER));

        return CrewRankedMemberResult.from(
                crewId,
                CrewRankKeyMapper.parseMemberId(tuple.getValue()),
                rank,
                rawScore,
                LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.systemDefault())
        );
    }
}
