package revi1337.onsquad.crew_hashtag.domain.repository;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import revi1337.onsquad.hashtag.domain.entity.Hashtag;
import revi1337.onsquad.hashtag.domain.entity.vo.HashtagType;

@ExtendWith(MockitoExtension.class)
class CrewHashtagRepositoryImplTest {

    @Mock
    private CrewHashtagJdbcRepository crewHashtagJdbcRepository;

    @Mock
    private CrewHashtagJpaRepository crewHashtagJpaRepository;

    @InjectMocks
    private CrewHashtagRepositoryImpl crewHashtagRepository;

    @Test
    @DisplayName("batchInsert 위임에 성공한다.")
    void batchInsert() {
        Long crewId = 1L;
        List<Hashtag> hashtags = List.of(Hashtag.fromHashtagType(HashtagType.ACTIVE));
        doNothing().when(crewHashtagJdbcRepository).batchInsert(crewId, hashtags);

        crewHashtagRepository.batchInsert(crewId, hashtags);

        verify(crewHashtagJdbcRepository).batchInsert(crewId, hashtags);
    }

    @Test
    @DisplayName("deleteByCrewId 위임에 성공한다.")
    void deleteByCrewId() {
        Long crewId = 1L;
        doNothing().when(crewHashtagJpaRepository).deleteById(crewId);

        crewHashtagRepository.deleteByCrewId(crewId);

        verify(crewHashtagJpaRepository).deleteById(crewId);
    }
}
