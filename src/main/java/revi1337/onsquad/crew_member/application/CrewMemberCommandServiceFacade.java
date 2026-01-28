package revi1337.onsquad.crew_member.application;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.StaleObjectStateException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrewMemberCommandServiceFacade {

    private final CrewMemberCommandService commandService;

    @Retryable(
            retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class, StaleObjectStateException.class},
            maxAttempts = 4,
            backoff = @Backoff(
                    delay = 20,
                    maxDelay = 100,
                    random = true
            ),
            recover = "recoverDelegateOwner"
    )
    public void delegateOwner(Long memberId, Long crewId, Long targetMemberId) {
        commandService.delegateOwner(memberId, crewId, targetMemberId);
    }

    @Retryable(
            retryFor = {OptimisticLockException.class, ObjectOptimisticLockingFailureException.class, StaleObjectStateException.class},
            maxAttempts = 4,
            backoff = @Backoff(
                    delay = 20,
                    maxDelay = 100,
                    random = true
            ),
            recover = "recoverKickOutMember"
    )
    public void kickOutMember(Long memberId, Long crewId, Long targetMemberId) {
        commandService.kickOutMember(memberId, crewId, targetMemberId);
    }

    @Recover
    public void recoverDelegateOwner(Throwable throwable, Long memberId, Long crewId, Long targetMemberId) {
        log.error("[DelegateOwner-Final-Failure] All retry attempts exhausted. Reason: {}, CrewId: {}, MemberId: {}, TargetMemberId: {}",
                throwable.getMessage(), crewId, memberId, targetMemberId);
    }

    @Recover
    public void recoverKickOutMember(Throwable throwable, Long memberId, Long crewId, Long targetMemberId) {
        log.error("[KickOutMember-Final-Failure] All retry attempts exhausted. Reason: {}, CrewId: {}, MemberId: {}, TargetMemberId: {}",
                throwable.getMessage(), crewId, memberId, targetMemberId);
    }
}
