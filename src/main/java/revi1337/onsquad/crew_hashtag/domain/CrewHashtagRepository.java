package revi1337.onsquad.crew_hashtag.domain;

import java.util.List;
import revi1337.onsquad.hashtag.domain.Hashtag;

public interface CrewHashtagRepository {

    void batchInsertCrewHashtags(Long crewId, List<Hashtag> hashtags);

    void deleteAllByIdIn(List<Long> ids);

}
