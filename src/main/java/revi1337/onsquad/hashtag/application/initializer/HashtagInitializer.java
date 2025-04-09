package revi1337.onsquad.hashtag.application.initializer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import revi1337.onsquad.hashtag.application.HashtagService;

@RequiredArgsConstructor
@Configuration
public class HashtagInitializer {

    private final HashtagService cachedHashtagService;

    @EventListener(ApplicationReadyEvent.class)
    private void init() {
        cachedHashtagService.findHashtags();
    }
}
