package revi1337.onsquad.crew_member.application.leaderboard;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import revi1337.onsquad.crew_member.config.CrewLeaderboardProperties;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.model.CrewRankerDetail;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrewLeaderboardRebuildService {

    private final MemberRepository memberRepository;
    private final CrewRankerRepository crewRankerRepository;
    private final CrewLeaderboardBackupManager leaderboardBackupManager;
    private final CrewLeaderboardProperties leaderboardProperties;

    @Deprecated
    public void renewTopRankers(LocalDateTime from, LocalDateTime to, Integer rankLimit) {
        try {
            List<CrewRanker> rankedMembers = crewRankerRepository.fetchAggregatedRankedMembers(from, to, rankLimit).stream()
                    .map(CrewRankerDetail::toEntity)
                    .toList();

            crewRankerRepository.truncate();
            crewRankerRepository.insertBatch(rankedMembers);
            log.info("[Successfully Renew CrewRanker In DataBase : {} ~ {}]", from, to);
        } catch (Exception exception) {
            log.error("[Fail to Renew CrewRanker In DataBase : {} ~ {}]", from, to, exception);
            throw exception;
        }
    }

    public void renewTopRankers(List<CrewRankerDetail> crewRankers) {
        try {
            List<Long> memberIds = getMemberIds(crewRankers);
            Map<Long, Member> memberMapping = getMemberLookupTable(memberIds);
            List<CrewRanker> newRankedMembers = getNewRankers(crewRankers, memberMapping);

            crewRankerRepository.truncate();
            crewRankerRepository.insertBatch(newRankedMembers);
            log.info("[Rebuild] Database sync complete. ({} members)", newRankedMembers.size());
        } catch (Exception exception) {
            log.error("[Rebuild] Failed to sync database. Attempting emergency recovery...", exception);
            attemptRecoveryFromBackup();
            throw exception;
        }
    }

    private void attemptRecoveryFromBackup() {
        try {
            List<CrewRankerDetail> backup = leaderboardBackupManager.getBackup();
            if (backup.isEmpty()) {
                log.warn("[Recovery] Fallback failed: No backup found in Redis.");
                return;
            }
            List<Long> memberIds = backup.stream()
                    .map(CrewRankerDetail::memberId)
                    .toList();

            Map<Long, Member> memberMapping = getMemberLookupTable(memberIds);
            List<CrewRanker> recoveredMembers = getNewRankers(backup, memberMapping);
            crewRankerRepository.insertBatch(recoveredMembers);
            log.info("[Recovery] Successfully restored {} members from Redis backup.", recoveredMembers.size());
        } catch (Exception recoveryException) {
            log.error("[Recovery] Fatal error during backup restoration!", recoveryException);
        }
    }

    private List<Long> getMemberIds(List<CrewRankerDetail> crewRankers) {
        return crewRankers.stream()
                .map(CrewRankerDetail::memberId)
                .toList();
    }

    private Map<Long, Member> getMemberLookupTable(List<Long> memberIds) {
        return memberRepository.findByIdIn(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, member -> member));
    }

    private List<CrewRanker> getNewRankers(List<CrewRankerDetail> crewRankers, Map<Long, Member> memberLookupTable) {
        Map<Long, List<CrewRankerDetail>> groupedByCrew = crewRankers.stream().collect(Collectors.groupingBy(CrewRankerDetail::crewId));
        List<CrewRanker> newRankedMembers = new ArrayList<>();
        for (Long crewId : groupedByCrew.keySet()) {
            List<CrewRankerDetail> rankers = groupedByCrew.get(crewId);
            int currentRank = 1;
            for (CrewRankerDetail candidate : rankers) {
                Member member = memberLookupTable.get(candidate.memberId());
                if (member != null && currentRank <= leaderboardProperties.rankLimit()) {
                    newRankedMembers.add(candidate.toEntity(currentRank++, member));
                }
            }
        }
        return newRankedMembers;
    }
}
