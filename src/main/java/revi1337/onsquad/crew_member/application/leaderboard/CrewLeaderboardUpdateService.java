package revi1337.onsquad.crew_member.application.leaderboard;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.config.CrewLeaderboardProperties;
import revi1337.onsquad.crew_member.domain.model.CrewLeaderboards;
import revi1337.onsquad.crew_member.domain.model.CrewRankerCandidate;
import revi1337.onsquad.crew_member.domain.model.RankerProfile;
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
    public void updateLeaderboards(CrewLeaderboards leaderboards) {
        crewRankerJpaRepository.deleteAllInBatch();
        if (leaderboards.isEmpty()) {
            return;
        }
        updateRankers(leaderboards);
    }

    private void updateRankers(CrewLeaderboards leaderboards) {
        try {
            List<Long> memberIds = leaderboards.getAllRankerIds();
            Map<Long, RankerProfile> memberMapping = memberRepository.findByIdIn(memberIds).stream()
                    .collect(Collectors.toMap(Member::getId, member -> new RankerProfile(
                            member.getNickname(),
                            member.getMbti()
                    )));

            List<CrewRankerCandidate> rankers = leaderboards.selectRankers(leaderboardProperties.rankLimit(), memberMapping);
            crewRankerJdbcRepository.insertBatch(rankers);
            log.info("[LeaderboardUpdate] Synchronization successful. ({} rankers persisted)", rankers.size());
        } catch (Exception exception) {
            log.error("[LeaderboardUpdate] Critical failure during RDB sync. Initiating emergency recovery...", exception);
            throw exception;
        }
    }
}
