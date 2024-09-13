package revi1337.onsquad.crew_hashtag.domain;

import revi1337.onsquad.hashtag.domain.Hashtag;

import java.util.List;

public interface CrewHashtagRepository {

    void batchInsertCrewHashtags(Long crewId, List<Hashtag> hashtags);

    void deleteAllByIdIn(List<Long> ids);

}
