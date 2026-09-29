package revi1337.onsquad.concurrency.infra;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import revi1337.onsquad.infrastructure.support.request.CaffeineRequestCacheHandler;

@Disabled("동시성 테스트는 스레드 간 격리 문제로 인해 수동 검증 시에만 단독 실행한다. (CI/CD 에서 문제 발생 가능)")
class CaffeineRequestCacheHandlerConcurrencyTest {

    @Test
    @DisplayName("같은 key로 두 요청이 동시에 들어와도 체크-저장 원자성이 보장되어 하나만 첫 요청으로 통과한다")
    void test() {
        CaffeineRequestCacheHandler handler = new CaffeineRequestCacheHandler();
        String key = "duplicate-request-key";
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        CompletableFuture<Boolean> future1 = CompletableFuture.supplyAsync(() -> {
            waitToStart(startLatch);
            return handler.isFirstRequest(key, "value", 1, TimeUnit.MINUTES);
        }, executor);
        CompletableFuture<Boolean> future2 = CompletableFuture.supplyAsync(() -> {
            waitToStart(startLatch);
            return handler.isFirstRequest(key, "value", 1, TimeUnit.MINUTES);
        }, executor);
        startLatch.countDown();
        CompletableFuture.allOf(future1, future2).join();
        executor.shutdown();

        assertThat(List.of(future1.join(), future2.join()))
                .as("원자적 putIfAbsent로 두 스레드 중 정확히 하나만 첫 요청으로 통과해야 한다")
                .containsExactlyInAnyOrder(true, false);
    }

    private void waitToStart(CountDownLatch start) {
        try {
            start.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
