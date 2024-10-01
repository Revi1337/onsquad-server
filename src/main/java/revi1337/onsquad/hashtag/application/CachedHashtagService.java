package revi1337.onsquad.hashtag.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class CachedHashtagService implements HashtagService {

    private final HashtagService defaultHashtagService;
    private final List<String> cachedHashtags = new ArrayList<>();

    @Override
    public List<String> findHashtags() {
        if (!cachedHashtags.isEmpty()) {
            log.info("[Cache Hit] Hashtags Cache Hit");
            return cachedHashtags;
        }

        cachedHashtags.addAll(defaultHashtagService.findHashtags());
        return cachedHashtags;
    }
}
