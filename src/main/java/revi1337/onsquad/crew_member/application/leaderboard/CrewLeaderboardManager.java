package revi1337.onsquad.crew_member.application.leaderboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;
import revi1337.onsquad.crew_member.domain.CrewActivity;
import revi1337.onsquad.crew_member.domain.result.CrewRankedMemberResult;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;
import revi1337.onsquad.infrastructure.storage.redis.RedisScanUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrewLeaderboardManager {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final long MULTIPLIER = 10_000_000_000L;
    private static final long BASE_EPOCH_TIME = LocalDate.of(2026, 1, 1)
            .atStartOfDay(KST)
            .toEpochSecond();
    private static final RedisScript<Long> APPLY_SCORE_SCRIPT = RedisScript.of(new ClassPathResource("db/redis/apply_score.lua"), Long.class);

    private final ObjectMapper defaultObjectMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public void applyActivity(Long crewId, Long memberId, Instant applyAt, CrewActivity crewActivity) {
        String namedSortedSet = CrewLeaderboardKeyMapper.toLeaderboardKey(crewId);
        String specificName = CrewLeaderboardKeyMapper.toMemberKey(memberId);

        stringRedisTemplate.execute(
                APPLY_SCORE_SCRIPT,
                Collections.singletonList(namedSortedSet),
                specificName,
                String.valueOf(crewActivity.getScore()),
                String.valueOf(applyAt.getEpochSecond()),
                String.valueOf(MULTIPLIER),
                String.valueOf(BASE_EPOCH_TIME)
        );

        log.debug("Applying activity score for member in crew - member: {}, crew: {}", memberId, crewId);
    }

    public List<CrewRankedMemberResult> getLeaderboard(int rankLimit) {
        List<String> computedKeys = RedisScanUtils.scanKeys(stringRedisTemplate, CrewLeaderboardKeyMapper.getLeaderboardPattern());
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
            Long crewId = CrewLeaderboardKeyMapper.parseCrewIdFromKey(computedKeys.get(i));
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

    public void backupPreviousLeaderboard(List<CrewRankedMemberResult> previousRankedMembers) {
        Map<Long, List<CrewRankedMemberResult>> groupedMembers = previousRankedMembers.stream()
                .collect(Collectors.groupingBy(CrewRankedMemberResult::crewId));

        stringRedisTemplate.executePipelined((RedisCallback<Void>) connection -> {
            groupedMembers.forEach((key, results) -> {
                try {
                    String previousCrewRankKey = CrewLeaderboardKeyMapper.toPreviousLeaderboardKey(key);
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

    public void removeLeaderboards(List<Long> crewIds) {
        List<String> namedSortedSets = CrewLeaderboardKeyMapper.toLeaderboardKeys(crewIds);
        RedisCacheEvictor.unlinkKeys(stringRedisTemplate, namedSortedSets);
    }

    private CrewRankedMemberResult convertToResult(Long crewId, int rank, TypedTuple<String> tuple) {
        double compositeScore = tuple.getScore();
        long roundedCompositeScore = Math.round(compositeScore);
        long rawScore = roundedCompositeScore / MULTIPLIER;
        long relativeEpochSecond = roundedCompositeScore % MULTIPLIER;
        long originalEpochSecond = relativeEpochSecond + BASE_EPOCH_TIME;

        return CrewRankedMemberResult.from(
                crewId,
                CrewLeaderboardKeyMapper.parseMemberIdFromKey(tuple.getValue()),
                rank,
                rawScore,
                LocalDateTime.ofInstant(Instant.ofEpochSecond(originalEpochSecond), KST)
        );
    }
}
