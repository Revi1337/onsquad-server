package revi1337.onsquad.crew.domain;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import revi1337.onsquad.crew.domain.result.CrewResult;
import revi1337.onsquad.crew_hashtag.domain.CrewHashtags;
import revi1337.onsquad.hashtag.domain.entity.vo.HashtagType;

public final class CrewResults {

    private final List<CrewResult> results;

    public CrewResults(List<CrewResult> results) {
        this.results = Collections.unmodifiableList(results);
    }

    public List<Long> getIds() {
        return results.stream()
                .map(CrewResult::getId)
                .toList();
    }

    public List<CrewResult> values() {
        return results;
    }

    public boolean isNotEmpty() {
        return !results.isEmpty();
    }

    public void linkHashtags(CrewHashtags hashtags) {
        Map<Long, List<HashtagType>> hashtagMap = hashtags.groupByCrewId();
        results.forEach(result -> {
            List<HashtagType> hashtagTypes = hashtagMap.get(result.getId());
            if (hashtagTypes != null) {
                result.addHashtagTypes(hashtagTypes);
            }
        });
    }
}
