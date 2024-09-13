package revi1337.onsquad.hashtag.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import revi1337.onsquad.hashtag.domain.HashtagRepository;

@RequiredArgsConstructor
@Configuration
public class HashtagInitializer {

    private final HashtagRepository hashtagRepository;

    @EventListener(ApplicationReadyEvent.class)
    private void init() {
        hashtagRepository.findAll();
    }
}
