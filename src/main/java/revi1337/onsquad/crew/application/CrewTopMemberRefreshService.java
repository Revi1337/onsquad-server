package revi1337.onsquad.crew.application;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import revi1337.onsquad.crew.domain.dto.top.Top5CrewMemberDomainDto;
import revi1337.onsquad.crew.domain.repository.top.CrewTopMemberRepository;

/**
 * Service responsible for aggregating and refreshing top-ranked members in each crew.
 *
 * <p>This service recalculates crew member rankings for a given period and
 * updates the persisted ranking data in batch.</p>
 *
 * <p>The refresh operation is triggered by {@link revi1337.onsquad.crew.application.scheduler.CrewTopMemberRefreshScheduler}
 * and executed asynchronously using a dedicated worker thread pool.</p>
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CrewTopMemberRefreshService {

    private final CrewTopMemberRepository crewTopMemberRepository;

    @Transactional
    @Async("crewTopMemberRefreshExecutor")
    public void refresh(LocalDate from, LocalDate to, Integer rankLimit) {
        try {
            crewTopMemberRepository.deleteAllInBatch();
            crewTopMemberRepository.batchInsert(
                    crewTopMemberRepository.fetchAggregatedTopMembers(from, to, rankLimit).stream()
                            .map(Top5CrewMemberDomainDto::toEntity)
                            .toList()
            );
            log.info("[Successfully Renew CrewTopMember In DataBase : {} ~ {}]", from, to);
        } catch (Exception exception) {
            log.error("[Fail to Renew CrewTopMember In DataBase : {} ~ {}]", from, to);
        }
    }
}
