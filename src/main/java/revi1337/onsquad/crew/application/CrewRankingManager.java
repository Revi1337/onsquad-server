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
import revi1337.onsquad.common.constant.CacheConst;
import revi1337.onsquad.common.constant.CacheConst.CacheFormat;
import revi1337.onsquad.common.constant.Sign;
import revi1337.onsquad.crew_member.domain.CrewActivity;
import revi1337.onsquad.crew_member.domain.result.CrewRankedMemberResult;
import revi1337.onsquad.infrastructure.redis.RedisScanUtils;

@RequiredArgsConstructor
@Component
public class CrewRankingManager {

    private static final long MULTIPLIER = 10_000_000_000L;

    private final StringRedisTemplate stringRedisTemplate;

    public void applyActivityScore(Long crewId, Long memberId, Instant applyAt, CrewActivity crewActivity) {
        String namedSortedSet = generateZSetKey(crewId);
        String specificName = generateZSetMemberKey(memberId);

        double currentWeight = getCurrentWeight(namedSortedSet, specificName);
        double nextWeight = calculateNextWeight(currentWeight, crewActivity.getScore(), applyAt.getEpochSecond());

        stringRedisTemplate.opsForZSet().add(namedSortedSet, specificName, nextWeight);
    }

    public List<CrewRankedMemberResult> getRankedMembers(int rankLimit) {
        List<CrewRankedMemberResult> rankedMembers = new ArrayList<>();
        RedisScanUtils.scan(
                stringRedisTemplate,
                generateCrewPattern(),
                RedisScanUtils.DEFAULT_SCAN_SIZE,
                crewKey -> processCrewRanking(crewKey, rankLimit, rankedMembers)
        );

        return rankedMembers;
    }

    private String generateZSetKey(Long crewId) {
        return String.format(CacheFormat.CREW_COMPLEX, crewId, CacheConst.RANK_MEMBERS);
    }

    private String generateZSetMemberKey(Long memberId) {
        return String.format("member:%s", memberId);
    }

    private Long parseMemberIdFromMemberKey(String memberKey) {
        return Long.parseLong(memberKey.replace("member:", Sign.EMPTY));
    }

    private String generateCrewPattern() {
        return String.format(CacheFormat.CREW_COMPLEX, Sign.ASTERISK, CacheConst.RANK_MEMBERS);
    }

    private Long parseCrewIdFromKey(String key) {
        return Long.parseLong(key.split(Sign.COLON)[2]);
    }

    private double getCurrentWeight(String namedSortedSet, String specificName) {
        return Objects.requireNonNullElse(stringRedisTemplate.opsForZSet().score(namedSortedSet, specificName), 0).doubleValue();
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
        Long crewId = parseCrewIdFromKey(crewKey);
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
                parseMemberIdFromMemberKey(tuple.getValue()),
                rank,
                rawScore,
                LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.systemDefault())
        );
    }
}
