package revi1337.onsquad.crew_member.application.leaderboard;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import revi1337.onsquad.crew_member.domain.model.CrewActivity;
import revi1337.onsquad.crew_member.domain.model.CrewLeaderboard;
import revi1337.onsquad.crew_member.domain.model.CrewLeaderboards;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.infrastructure.storage.redis.RedisCacheEvictor;
import revi1337.onsquad.infrastructure.storage.redis.RedisDataStructureUtils;
import revi1337.onsquad.infrastructure.storage.redis.RedisDataStructureUtils.ZSetRange;
import revi1337.onsquad.infrastructure.storage.redis.RedisScanUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrewLeaderboardManager {

    public static final int RANKING_OVER_FETCH_SIZE = 50;
    private static final RedisScript<Long> APPLY_SCORE_SCRIPT = RedisScript.of(new ClassPathResource("db/redis/apply_score.lua"), Long.class);

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
                String.valueOf(CompositeScore.MULTIPLIER),
                String.valueOf(CompositeScore.BASE_EPOCH_TIME)
        );
    }

    public CrewLeaderboard getLeaderboard(Long crewId, int topNInclusive) {
        return getLeaderboard(crewId, 1, topNInclusive);
    }

    public CrewLeaderboard getLeaderboard(Long crewId, int startRank, int endRank) {
        ZSetRange range = RedisDataStructureUtils.toZSetRange(startRank, endRank);
        Set<TypedTuple<String>> tuples = stringRedisTemplate.opsForZSet()
                .reverseRangeWithScores(CrewLeaderboardKeyMapper.toLeaderboardKey(crewId), range.start(), range.end());

        if (CollectionUtils.isEmpty(tuples)) {
            return CrewLeaderboard.empty();
        }

        return convertToLeaderboard(crewId, startRank, tuples);
    }

    public CrewLeaderboards getAllLeaderboards(int topN) {
        List<String> computedKeys = RedisScanUtils.scanKeys(stringRedisTemplate, CrewLeaderboardKeyMapper.getLeaderboardPattern());
        if (computedKeys.isEmpty()) {
            return CrewLeaderboards.empty();
        }

        ZSetRange range = RedisDataStructureUtils.toZSetRange(1, topN);
        List<Object> pipelinedResults = stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            computedKeys.stream()
                    .map(computedKey -> stringRedisTemplate.getStringSerializer().serialize(computedKey))
                    .forEach(serializedKey -> connection.zSetCommands().zRevRangeWithScores(serializedKey, range.start(), range.end()));
            return null;
        });

        Map<Long, CrewLeaderboard> leaderboards = new HashMap<>();
        for (int i = 0; i < computedKeys.size(); i++) {
            if (!(pipelinedResults.get(i) instanceof Set<?> rawSet)) {
                continue;
            }
            Long crewId = CrewLeaderboardKeyMapper.parseCrewIdFromKey(computedKeys.get(i));
            leaderboards.put(crewId, convertToLeaderboard(crewId, 1, (Set<TypedTuple<String>>) rawSet));
        }

        return new CrewLeaderboards(leaderboards);
    }

    public void removeAllLeaderboards() {
        List<String> computedKeys = RedisScanUtils.scanKeys(stringRedisTemplate, CrewLeaderboardKeyMapper.getLeaderboardPattern());
        RedisCacheEvictor.unlinkKeys(stringRedisTemplate, computedKeys);
    }

    public void removeLeaderboards(List<Long> crewIds) {
        List<String> namedSortedSets = CrewLeaderboardKeyMapper.toLeaderboardKeys(crewIds);
        RedisCacheEvictor.unlinkKeys(stringRedisTemplate, namedSortedSets);
    }

    public long getScore(Long crewId, Long memberId) {
        Double compositeScore = stringRedisTemplate.opsForZSet().score(
                CrewLeaderboardKeyMapper.toLeaderboardKey(crewId), CrewLeaderboardKeyMapper.toMemberKey(memberId)
        );
        if (compositeScore == null) {
            return 0L;
        }

        return CompositeScore.from(compositeScore).getActualScore();
    }

    private CrewLeaderboard convertToLeaderboard(Long crewId, int startRank, Set<TypedTuple<String>> tuples) {
        int currentRank = startRank;
        List<CrewRankerCandidate> candidates = new ArrayList<>(tuples.size());
        for (TypedTuple<String> tuple : tuples) {
            CompositeScore compositeScore = CompositeScore.from(tuple.getScore());
            candidates.add(CrewRankerCandidate.from(
                    crewId,
                    CrewLeaderboardKeyMapper.parseMemberIdFromKey(tuple.getValue()),
                    currentRank++,
                    compositeScore.getActualScore(),
                    compositeScore.getActivityTime()
            ));
        }

        return new CrewLeaderboard(candidates);
    }
}
