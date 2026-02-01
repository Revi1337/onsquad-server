package revi1337.onsquad.crew_hashtag.domain.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.crew_hashtag.domain.entity.CrewHashtag;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;

@Repository
@RequiredArgsConstructor
public class CrewHashtagRepositoryImpl implements CrewHashtagRepository {

    private final CrewHashtagJdbcRepository crewHashtagJdbcRepository;
    private final CrewHashtagJpaRepository crewHashtagJpaRepository;

    @Override
    public void insertBatch(Long crewId, List<Hashtag> hashtags) {
        crewHashtagJdbcRepository.insertBatch(crewId, hashtags);
    }

    @Override
    public List<CrewHashtag> fetchHashtagsByCrewIdIn(List<Long> crewIds) {
        return crewHashtagJpaRepository.fetchHashtagsByCrewIdIn(crewIds);
    }

    @Override
    public int deleteByCrewIdIn(List<Long> crewIds) {
        return crewHashtagJpaRepository.deleteByCrewIdIn(crewIds);
    }
}
