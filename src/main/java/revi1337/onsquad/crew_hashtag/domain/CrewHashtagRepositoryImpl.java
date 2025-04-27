package revi1337.onsquad.crew_hashtag.domain;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import revi1337.onsquad.hashtag.domain.Hashtag;

@RequiredArgsConstructor
@Repository
public class CrewHashtagRepositoryImpl implements CrewHashtagRepository {

    private final CrewHashtagJdbcRepository crewHashtagJdbcRepository;
    private final CrewHashtagJpaRepository crewHashtagJpaRepository;

    @Override
    public void batchInsert(Long crewId, List<Hashtag> hashtags) {
        crewHashtagJdbcRepository.batchInsert(crewId, hashtags);
    }

    @Override
    public void deleteByCrewId(Long crewId) {
        crewHashtagJpaRepository.deleteByCrewId(crewId);
    }
}
