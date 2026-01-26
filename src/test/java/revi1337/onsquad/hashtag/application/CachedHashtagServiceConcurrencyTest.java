package revi1337.onsquad.hashtag.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CachedHashtagServiceConcurrencyTest {

    private final HashtagService defaultHashtagService = mock(HashtagService.class);

    @Test
    @DisplayName("동시에 여러 요청이 들어와도 데이터가 중복으로 적재되지 않아야 한다.")
    void concurrency() {
        List<String> mockHashtags = List.of("태그1", "태그2");
        given(defaultHashtagService.findHashtags()).willReturn(mockHashtags);
        CachedHashtagService service = new CachedHashtagService(defaultHashtagService);

        int threadCount = 3;
        CountDownLatch start = new CountDownLatch(1);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    start.await();
                    service.findHashtags();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }));
        }
        start.countDown();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        List<String> result = service.findHashtags();
        assertThat(result).hasSize(mockHashtags.size()).containsExactlyElementsOf(mockHashtags);
    }
}
