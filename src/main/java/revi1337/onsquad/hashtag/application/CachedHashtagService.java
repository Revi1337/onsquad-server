package revi1337.onsquad.hashtag.application;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CachedHashtagService implements HashtagService {

    private final List<String> cachedHashtags;

    public CachedHashtagService(@Qualifier("defaultHashtagService") HashtagService delegate) {
        this.cachedHashtags = Collections.unmodifiableList(delegate.findHashtags());
    }

    @Override
    public List<String> findHashtags() {
        return cachedHashtags;
    }
}
