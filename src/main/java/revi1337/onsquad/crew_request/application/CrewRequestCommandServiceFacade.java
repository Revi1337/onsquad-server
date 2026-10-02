package revi1337.onsquad.crew_request.application;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrewRequestCommandServiceFacade {

    private static final int MAX_RETRY_COUNT = 4;

    private final CrewRequestCommandService crewRequestCommandService;
    private final AtomicInteger retryCounter = new AtomicInteger(0);
    private final AtomicInteger failCounter = new AtomicInteger(0);

    @Retryable(
            maxAttempts = MAX_RETRY_COUNT,
            backoff = @Backoff(
                    delay = 20,
                    maxDelay = 100,
                    random = true
            )
    )
    public void acceptRequest(Long memberId, Long crewId, Long requestId) {
        log.info("retryCount: {}", retryCounter.getAndIncrement());
        crewRequestCommandService.acceptRequest(memberId, crewId, requestId);
    }

    @Recover
    public void recover(Throwable throwable, Long memberId, Long crewId, Long requestId) {
        log.info("failCount: {}", failCounter.incrementAndGet());
        throw new IllegalStateException("가입 승인 처리에 실패했습니다. 다시 시도해주세요.", throwable);
    }
}
