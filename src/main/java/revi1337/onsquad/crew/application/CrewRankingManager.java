package revi1337.onsquad.crew.application;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;
import revi1337.onsquad.crew.util.CrewRankKeyMapper;
import revi1337.onsquad.crew_member.domain.CrewActivity;
import revi1337.onsquad.crew_member.domain.result.CrewRankedMemberResult;
import revi1337.onsquad.infrastructure.redis.RedisScanUtils;

@RequiredArgsConstructor
@Component
public class CrewRankingManager {

    private static final long MULTIPLIER = 10_000_000_000L;

    private final StringRedisTemplate stringRedisTemplate;

    public void applyActivityScore(Long crewId, Long memberId, Instant applyAt, CrewActivity crewActivity) {
        String namedSortedSet = CrewRankKeyMapper.toCrewRankKey(crewId);
        String specificName = CrewRankKeyMapper.toMemberKey(memberId);

        double currentWeight = getCurrentWeight(namedSortedSet, specificName);
        double nextWeight = calculateNextWeight(currentWeight, crewActivity.getScore(), applyAt.getEpochSecond());

        stringRedisTemplate.opsForZSet().add(namedSortedSet, specificName, nextWeight);
    }

    public List<CrewRankedMemberResult> getRankedMembers(int rankLimit) {
        List<CrewRankedMemberResult> rankedMembers = new ArrayList<>();
        RedisScanUtils.scan(
                stringRedisTemplate,
                CrewRankKeyMapper.getCrewRankPattern(),
                crewKey -> processCrewRanking(crewKey, rankLimit, rankedMembers)
        );

        return rankedMembers;
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

    private void processCrewRanking(String crewKey, int rankLimit, List<CrewRankedMemberResult> results) {
        Set<TypedTuple<String>> rankings = stringRedisTemplate.opsForZSet().reverseRangeWithScores(crewKey, 0, rankLimit - 1);
        if (rankings == null) {
            return;
        }
        Long crewId = CrewRankKeyMapper.parseCrewId(crewKey);
        int rank = 1;
        for (TypedTuple<String> tuple : rankings) {
            results.add(convertToResult(crewId, rank++, tuple));
        }
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
