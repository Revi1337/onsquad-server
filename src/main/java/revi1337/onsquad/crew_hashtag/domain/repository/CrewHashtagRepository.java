package revi1337.onsquad.crew_hashtag.domain.repository;

import java.util.List;
import revi1337.onsquad.crew_hashtag.domain.entity.CrewHashtag;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;

public interface CrewHashtagRepository {

    void insertBatch(Long crewId, List<Hashtag> hashtags);

    List<CrewHashtag> fetchHashtagsByCrewIdIn(List<Long> crewIds);

    void deleteByCrewId(Long crewId);

}
