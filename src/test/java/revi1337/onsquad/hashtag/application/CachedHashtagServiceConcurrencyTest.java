package revi1337.onsquad.hashtag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CachedHashtagServiceConcurrencyTest {

    @Mock
    private HashtagService defaultHashtagService;

    @InjectMocks
    private CachedHashtagService cachedHashtagService;

    @Test
    @DisplayName("동시에 여러 요청이 들어오면 데이터가 중복으로 적재될 수 있다. (HashtagInitializer 의존하지 않으면 동시성 문제)")
    void concurrency() {
        // given
        List<String> mockHashtags = List.of("태그1", "태그2");
        given(defaultHashtagService.findHashtags()).willReturn(mockHashtags);

        // when
        int threadCount = 3;
        CountDownLatch start = new CountDownLatch(1);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    start.await();
                    cachedHashtagService.findHashtags();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }
        start.countDown();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // then
        List<String> result = cachedHashtagService.findHashtags();
        assertThat(result).as("동시성 땜에 4 또는 6").hasSizeGreaterThan(2);
    }
}
