package revi1337.onsquad.crew_member.application.leaderboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.config.CrewLeaderboardProperties;
import revi1337.onsquad.crew_member.domain.entity.CrewRanker;
import revi1337.onsquad.crew_member.domain.model.CrewRankerDetail;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerJdbcRepository;
import revi1337.onsquad.crew_member.domain.repository.rank.CrewRankerJpaRepository;
import revi1337.onsquad.member.domain.entity.Member;
import revi1337.onsquad.member.domain.repository.MemberJpaRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CrewLeaderboardUpdateService {

    private final MemberJpaRepository memberRepository;
    private final CrewRankerJpaRepository crewRankerJpaRepository;
    private final CrewRankerJdbcRepository crewRankerJdbcRepository;
    private final CrewLeaderboardProperties leaderboardProperties;

    @Transactional
    public void update(List<CrewRankerDetail> rankers) {
        List<CrewRanker> previousRankerSnapshots = crewRankerJpaRepository.findAll();
        if (previousRankerSnapshots.isEmpty()) {
            return;
        }
        crewRankerJpaRepository.deleteAllInBatch();
        updateRankers(rankers);
    }

    private void updateRankers(List<CrewRankerDetail> rankers) {
        try {
            List<Long> memberIds = collectRankerIds(rankers);
            Map<Long, Member> memberMapping = prepareRankerLookupTable(memberIds);
            List<CrewRanker> newRankedMembers = getNewRankers(rankers, memberMapping);
            crewRankerJdbcRepository.insertBatch(newRankedMembers);
            log.info("[LeaderboardUpdate] Synchronization successful. ({} rankers persisted)", newRankedMembers.size());
        } catch (Exception exception) {
            log.error("[LeaderboardUpdate] Critical failure during RDB sync. Initiating emergency recovery...", exception);
            throw exception;
        }
    }

    private List<Long> collectRankerIds(List<CrewRankerDetail> rankers) {
        return rankers.stream()
                .map(CrewRankerDetail::memberId)
                .toList();
    }

    private Map<Long, Member> prepareRankerLookupTable(List<Long> memberIds) {
        return memberRepository.findByIdIn(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, member -> member));
    }

    private List<CrewRanker> getNewRankers(List<CrewRankerDetail> rankers, Map<Long, Member> memberLookupTable) {
        Map<Long, List<CrewRankerDetail>> groupedByCrew = rankers.stream().collect(Collectors.groupingBy(CrewRankerDetail::crewId));
        List<CrewRanker> newRankedMembers = new ArrayList<>();
        for (Long crewId : groupedByCrew.keySet()) {
            int currentRank = 1;
            for (CrewRankerDetail candidate : groupedByCrew.get(crewId)) {
                Member member = memberLookupTable.get(candidate.memberId());
                if (member != null && currentRank <= leaderboardProperties.rankLimit()) {
                    newRankedMembers.add(candidate.toEntity(currentRank++, member));
                }
            }
        }
        return newRankedMembers;
    }
}
