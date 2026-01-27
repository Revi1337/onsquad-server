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

    private static final int MAX_RETRY_COUNT = 20;

    private final CrewRequestCommandService crewRequestCommandService;
    private final AtomicInteger retryCounter = new AtomicInteger(0);

    @Retryable(
            maxAttempts = MAX_RETRY_COUNT,
            backoff = @Backoff(
                    delay = 50,
                    maxDelay = 500,
                    random = true
            )
    )
    public void acceptRequest(Long memberId, Long crewId, Long requestId) {
        log.info("retryCount: {}", retryCounter.incrementAndGet());
        crewRequestCommandService.acceptRequest(memberId, crewId, requestId);
    }

    @Recover
    public void recover(Throwable throwable, Long memberId, Long crewId, Long requestId) {
        log.error("memberId = {}, crewId = {}, requestId = {}", memberId, crewId, requestId);
        // 총 149 번의 재시도 == DB 커넥션 149개 낭비 --> 응답 시간 너무 늦어짐 --> 프론트는 계속 기다림 --> 쀍.
        // 보상 Tx 알림등 부가적인 요소가 필요. 가입 승인인데 여기까지 필요한게 과연맞나..?
    }
}
