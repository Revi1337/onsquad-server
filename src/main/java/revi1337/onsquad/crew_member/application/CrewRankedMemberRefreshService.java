package revi1337.onsquad.crew_member.application;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew_member.application.scheduler.CrewRankedMemberRefreshScheduler;
import revi1337.onsquad.crew_member.domain.repository.top.CrewRankedMemberRepository;
import revi1337.onsquad.crew_member.domain.result.CrewRankedMemberResult;

/**
 * Service responsible for aggregating and refreshing top-ranked members in each crew.
 *
 * <p>This service recalculates crew member rankings for a given period and
 * updates the persisted ranking data in batch.</p>
 *
 * <p>The refresh operation is triggered by {@link CrewRankedMemberRefreshScheduler}
 * and executed asynchronously using a dedicated worker thread pool.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CrewRankedMemberRefreshService {

    private final CrewRankedMemberRepository crewRankedMemberRepository;

    @Transactional
    @Async("crewRankedMemberRefreshExecutor")
    public void refresh(LocalDate from, LocalDate to, Integer rankLimit) {
        try {
            crewRankedMemberRepository.deleteAllInBatch();
            crewRankedMemberRepository.insertBatch(
                    crewRankedMemberRepository.fetchAggregatedRankedMembers(from, to, rankLimit).stream()
                            .map(CrewRankedMemberResult::toEntity)
                            .toList()
            );
            log.info("[Successfully Renew CrewRankedMember In DataBase : {} ~ {}]", from, to);
        } catch (Exception exception) {
            log.error("[Fail to Renew CrewRankedMember In DataBase : {} ~ {}]", from, to, exception);
        }
    }
}
